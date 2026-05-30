/**
 * MRP智能补货引擎 —— 系统核心业务逻辑
 * 完整5步流程：数据清洗 → 需求预测 → 安全库存计算 → 净需求计算 → 订单生成+日志记录
 * 使用指数平滑算法(α自动寻优)和工业级安全库存公式
 * 整个流程在 @Transactional 事务中执行
 */

package com.kunlun.kunlun_backend.service.mrp;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kunlun.kunlun_backend.algorithm.forecast.ExponentialSmoothing;
import com.kunlun.kunlun_backend.algorithm.inventory.SafetyStockCalculator;
import com.kunlun.kunlun_backend.dto.MrpResult;
import com.kunlun.kunlun_backend.entity.*;
import com.kunlun.kunlun_backend.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MrpService {

    private final SalesHistoryMapper salesHistoryMapper;
    private final StationInventoryMapper inventoryMapper;
    private final PlannedOrderMapper orderMapper;
    private final StationMapper stationMapper;
    private final MaterialMapper materialMapper;
    private final PredictionLogMapper predictionLogMapper;
    private final DataCleaningService dataCleaningService;

    private static final int DEFAULT_FORECAST_DAYS = 3;
    private static final int HISTORY_DAYS = 90;

    /**
     * 完整MRP流程（5步）
     */
    @Transactional
    public MrpResult runMrp(Integer stationId, Integer materialId, int forecastDays) {
        // 步骤1：数据准备与清洗
                // 步骤1：数据准备与清洗（缺失日期填充 + 异常值检测）
        List<SalesHistory> cleanedData = dataCleaningService.getCleanedData(stationId, materialId, HISTORY_DAYS);
        dataCleaningService.markAnomalies(cleanedData);
        List<Double> volumes = dataCleaningService.toValueList(cleanedData, true);

        if (volumes.size() < 7) {
            throw new RuntimeException("历史数据不足，至少需要7天有效数据");
        }

        // 步骤2：需求预测（指数平滑α自动寻优）
                // 步骤2：需求预测（一/二次指数平滑α自动寻优，选MSE最小模型）
        ExponentialSmoothing.ForecastResult forecastResult = ExponentialSmoothing.predict(volumes, forecastDays);
        BigDecimal forecastValue = BigDecimal.valueOf(forecastResult.getForecastValue())
                .setScale(2, RoundingMode.HALF_UP);

        // 步骤3：安全库存计算（完整工业级公式）
        Station station = stationMapper.selectById(stationId);
        Material material = materialMapper.selectById(materialId);
        double serviceLevel = station.getServiceLevel() != null
                ? station.getServiceLevel().doubleValue() : 0.95;
        double leadTime = material.getLeadTime() != null
                ? material.getLeadTime().doubleValue() : station.getDefaultLeadTime().doubleValue();
        double stdLeadTime = material.getLeadTimeStd() != null
                ? material.getLeadTimeStd().doubleValue() : station.getDefaultLeadTimeStd().doubleValue();
        double stdDemand = dataCleaningService.calcStd(volumes);
        double avgDemand = dataCleaningService.calcMean(volumes);

                // 步骤3：安全库存计算（工业级公式 Is = h × √(T×σo² + Oa²×σT²)）
        BigDecimal safetyStock = SafetyStockCalculator.calcDailySafetyStock(
                serviceLevel, leadTime, stdDemand, avgDemand, stdLeadTime);

        // 步骤4：净需求计算与订单生成
        StationInventory inventory = getOrCreateInventory(stationId, materialId);
        BigDecimal currentStock = inventory.getCurrentStock() != null ? inventory.getCurrentStock() : BigDecimal.ZERO;
        BigDecimal onOrderStock = inventory.getOnOrderStock() != null ? inventory.getOnOrderStock() : BigDecimal.ZERO;
        BigDecimal deadStock = inventory.getDeadStock() != null ? inventory.getDeadStock() : BigDecimal.ZERO;

                // 步骤4：净需求 = 预测 - 当前库存 - 在途 + 安全库存 + 不可动
        BigDecimal netReq = SafetyStockCalculator.calcNetRequirement(
                forecastValue, currentStock, onOrderStock, safetyStock, deadStock);

        MrpResult.MrpResultBuilder resultBuilder = MrpResult.builder()
                .stationName(station.getName())
                .materialName(material.getName())
                .currentStock(currentStock)
                .deadStock(deadStock)
                .onOrderStock(onOrderStock)
                .safetyStock(safetyStock)
                .forecastValue(forecastValue)
                .netRequirement(netReq)
                .predictionMethod(forecastResult.getModelName())
                .predictionParams("{\"alpha\":" + String.format("%.2f", forecastResult.getAlpha())
                        + ",\"mse\":" + String.format("%.2f", forecastResult.getMse()) + "}")
                .mse(BigDecimal.valueOf(forecastResult.getMse()));

                // 净需求>0时生成补货订单，补货量取整到千升
        if (netReq.compareTo(BigDecimal.ZERO) > 0) {
            PlannedOrder order = new PlannedOrder();
            order.setStationId(stationId);
            order.setMaterialId(materialId);
            // 补货量取整到千升
            long roundedQty = (long) Math.ceil(netReq.doubleValue() / 1000) * 1000;
            order.setSuggestedQuantity(BigDecimal.valueOf(roundedQty));
            order.setRequiredDate(LocalDate.now().plusDays((long) leadTime));
            order.setStatus("PENDING");
            order.setPredictionMethod(forecastResult.getModelName());
            order.setPredictionParams(resultBuilder.build().getPredictionParams());
            order.setSafetyStockUsed(safetyStock);
            order.setNetRequirement(netReq);
            order.setForecastValue(forecastValue);
            order.setGeneratedBy("SYSTEM_MRP");
            orderMapper.insert(order);

            resultBuilder.orderGenerated(true)
                    .orderId(order.getId())
                    .suggestedQuantity(order.getSuggestedQuantity())
                    .requiredDate(order.getRequiredDate().toString());
        } else {
            resultBuilder.orderGenerated(false);
        }

        // 步骤5：记录预测日志（方法、参数、MSE、跟踪信号）
        PredictionLog log = new PredictionLog();
        log.setStationId(stationId);
        log.setMaterialId(materialId);
        log.setPredictDate(LocalDate.now());
        log.setModelName(forecastResult.getModelName());
        log.setParams("{\"alpha\":" + String.format("%.2f", forecastResult.getAlpha()) + "}");
        log.setForecastValue(forecastValue);
        log.setMse(clampDecimal(BigDecimal.valueOf(forecastResult.getMse()), 10, 4));
                predictionLogMapper.insert(log);

        // 更新库存表中的安全库存值
        inventory.setSafetyStock(safetyStock);
        inventoryMapper.updateById(inventory);

        return resultBuilder.build();
    }

    private StationInventory getOrCreateInventory(Integer stationId, Integer materialId) {
        StationInventory inv = inventoryMapper.selectOne(
                new LambdaQueryWrapper<StationInventory>()
                        .eq(StationInventory::getStationId, stationId)
                        .eq(StationInventory::getMaterialId, materialId));
        if (inv == null) {
            inv = new StationInventory();
            inv.setStationId(stationId);
            inv.setMaterialId(materialId);
            inv.setCurrentStock(BigDecimal.ZERO);
            inv.setDeadStock(BigDecimal.ZERO);
            inv.setOnOrderStock(BigDecimal.ZERO);
            inv.setSafetyStock(BigDecimal.ZERO);
            inventoryMapper.insert(inv);
        }
        return inv;
    }

    /**
     * 将 BigDecimal 限制在 DECIMAL(10,4) 范围内 (max: 999999.9999)
     */
    private BigDecimal clampDecimal(BigDecimal value, int precision, int scale) {
        if (value == null) return null;
        BigDecimal max = new BigDecimal("999999.9999");
        if (value.compareTo(max) > 0) return max;
        return value.setScale(scale, RoundingMode.HALF_UP);
    }
}
