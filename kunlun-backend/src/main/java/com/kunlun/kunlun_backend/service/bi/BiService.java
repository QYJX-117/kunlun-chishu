/**
 * BI看板数据聚合服务
 * 站点库存状态查询(含经纬度+健康度判定)
 * 销量趋势数据聚合
 * 数据健康度指标计算(完整性/及时性/异常率)
 * 预测准确率统计(按模型分类)
 */

package com.kunlun.kunlun_backend.service.bi;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kunlun.kunlun_backend.algorithm.inventory.SafetyStockCalculator;
import com.kunlun.kunlun_backend.dto.*;
import com.kunlun.kunlun_backend.entity.*;
import com.kunlun.kunlun_backend.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BiService {

    private final StationMapper stationMapper;
    private final StationInventoryMapper inventoryMapper;
    private final SalesHistoryMapper salesHistoryMapper;
    private final MaterialMapper materialMapper;
    private final PredictionLogMapper predictionLogMapper;
    private final PlannedOrderMapper orderMapper;

    /**
     * 所有站点的库存状态（含经纬度，用于地图展示）
     */
        /**
     * 所有营业中站点的库存状态(含经纬度+健康度判定，用于高德地图 Marker 渲染)
     * 颜色规则：当前库存 ≥ 2倍安全库存 → 绿色(SAFE)，≥ 1倍 → 黄色(WARNING)，不足 → 红色(DANGER)
     */
    public List<StationStatusVO> getStationStatus() {
        List<Station> stations = stationMapper.selectList(
                new LambdaQueryWrapper<Station>().eq(Station::getStatus, "营业中"));
        List<StationStatusVO> result = new ArrayList<>();

        for (Station station : stations) {
            List<StationInventory> invs = inventoryMapper.selectList(
                    new LambdaQueryWrapper<StationInventory>()
                            .eq(StationInventory::getStationId, station.getId()));
            for (StationInventory inv : invs) {
                Material mat = materialMapper.selectById(inv.getMaterialId());
                if (mat == null) continue;
                String status = SafetyStockCalculator.getStockStatus(
                        inv.getCurrentStock(), inv.getSafetyStock());
                result.add(StationStatusVO.builder()
                        .stationId(station.getId())
                        .name(station.getName())
                        .area(station.getArea())
                        .lng(station.getLongitude())
                        .lat(station.getLatitude())
                        .materialName(mat.getName())
                        .currentStock(inv.getCurrentStock())
                        .safetyStock(inv.getSafetyStock())
                        .stockStatus(status)
                        .build());
            }
        }
        return result;
    }

    /**
     * 某站点最近N天销量趋势
     */
        /**
     * 某站点最近N天销量趋势(排除异常值)
     */
    public List<SalesTrendVO> getRecentSales(Integer stationId, int days) {
        List<SalesHistory> list = salesHistoryMapper.selectList(
                new LambdaQueryWrapper<SalesHistory>()
                        .eq(SalesHistory::getStationId, stationId)
                        .eq(SalesHistory::getIsAnomaly, 0)
                        .ge(SalesHistory::getSaleDate, LocalDate.now().minusDays(days))
                        .orderByAsc(SalesHistory::getSaleDate));

        Map<Integer, String> matNameCache = new HashMap<>();
        return list.stream().map(s -> {
            String matName = matNameCache.computeIfAbsent(s.getMaterialId(),
                    mid -> materialMapper.selectById(mid).getName());
            return SalesTrendVO.builder()
                    .saleDate(s.getSaleDate())
                    .materialName(matName)
                    .salesVolume(s.getSalesVolume())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 数据健康度
     */
        /**
     * 数据健康度：完整性(有记录天数/应有天数)、最新日期、异常数量、准确率
     */
    public DataHealthVO getDataHealth() {
        List<SalesHistory> all = salesHistoryMapper.selectList(
                new LambdaQueryWrapper<SalesHistory>().ge(SalesHistory::getSaleDate, LocalDate.now().minusDays(90)));

        long totalDays = all.stream().map(SalesHistory::getSaleDate).distinct().count();
        long anomalyCount = all.stream().filter(s -> s.getIsAnomaly() == 1).count();
        LocalDate latestDate = all.stream().map(SalesHistory::getSaleDate).max(LocalDate::compareTo).orElse(null);

        long shouldDays = 90;
        BigDecimal completeness = BigDecimal.valueOf(totalDays * 100.0 / shouldDays)
                .setScale(1, RoundingMode.HALF_UP);

        long normalCount = all.stream().filter(s -> s.getIsAnomaly() == 0).count();
        BigDecimal accuracyRate = BigDecimal.valueOf(normalCount * 100.0 / Math.max(1, all.size()))
                .setScale(1, RoundingMode.HALF_UP);

        return DataHealthVO.builder()
                .completeness(completeness)
                .latestDate(latestDate)
                .anomalyCount(anomalyCount)
                .accuracyRate(accuracyRate)
                .build();
    }

    /**
     * 预测准确率统计
     */
        /**
     * 各预测模型准确率统计(按MAPE升序)
     */
    public List<ForecastAccuracyVO> getForecastAccuracy() {
        List<Map<String, Object>> stats = predictionLogMapper.modelAccuracyStats();
        return stats.stream().map(m -> ForecastAccuracyVO.builder()
                .modelName((String) m.get("model_name"))
                .mape(m.get("avg_mape") != null
                        ? new BigDecimal(m.get("avg_mape").toString()).setScale(2, RoundingMode.HALF_UP)
                        : null)
                .count((Long) m.get("count"))
                .build()).collect(Collectors.toList());
    }

    /**
     * 各站点库存对比
     */
        /**
     * 各站点库存对比数据(当前库存+安全库存+罐容)
     */
    public List<InventoryCompareVO> getInventoryCompare() {
        List<StationInventory> invs = inventoryMapper.selectList(Wrappers.emptyWrapper());
        List<InventoryCompareVO> result = new ArrayList<>();
        for (StationInventory inv : invs) {
            Station st = stationMapper.selectById(inv.getStationId());
            Material mat = materialMapper.selectById(inv.getMaterialId());
            if (st == null || mat == null) continue;
            result.add(InventoryCompareVO.builder()
                    .stationId(st.getId())
                    .stationName(st.getName())
                    .materialName(mat.getName())
                    .currentStock(inv.getCurrentStock())
                    .safetyStock(inv.getSafetyStock())
                    .tankCapacity(inv.getTankCapacity())
                    .build());
        }
        return result;
    }
}
