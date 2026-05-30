/**
 * 数据清洗服务 —— MRP流程第1步
 * 缺失日期前向填充(Forward Fill)
 * 3σ异常值自动检测与标记
 * 从SalesHistory提取数值列表用于算法计算
 */

package com.kunlun.kunlun_backend.service.mrp;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kunlun.kunlun_backend.entity.SalesHistory;
import com.kunlun.kunlun_backend.mapper.SalesHistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataCleaningService {

    private final SalesHistoryMapper salesHistoryMapper;

    /**
     * 获取清洗后的销量数据（填充缺失日期、标记异常值）
     */
        /**
     * 获取清洗后数据：生成连续日期序列，缺失值用前一天填充(Forward Fill)
     */
    public List<SalesHistory> getCleanedData(Integer stationId, Integer materialId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        List<SalesHistory> rawData = salesHistoryMapper.selectList(
                new LambdaQueryWrapper<SalesHistory>()
                        .eq(SalesHistory::getStationId, stationId)
                        .eq(SalesHistory::getMaterialId, materialId)
                        .ge(SalesHistory::getSaleDate, startDate)
                        .le(SalesHistory::getSaleDate, endDate)
                        .orderByAsc(SalesHistory::getSaleDate));

        Map<LocalDate, SalesHistory> dateMap = rawData.stream()
                .collect(Collectors.toMap(SalesHistory::getSaleDate, s -> s, (a, b) -> a));

        List<SalesHistory> result = new ArrayList<>();
        SalesHistory prev = null;
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            SalesHistory sh = dateMap.get(d);
            if (sh == null) {
                sh = new SalesHistory();
                sh.setStationId(stationId);
                sh.setMaterialId(materialId);
                sh.setSaleDate(d);
                sh.setSalesVolume(prev != null ? prev.getSalesVolume() : BigDecimal.ZERO);
                sh.setIsAnomaly(0);
                sh.setSource("前向填充");
            }
            result.add(sh);
            prev = sh;
        }
        return result;
    }

    /**
     * 3σ异常检测：标记超出3倍标准差的记录
     */
        /**
     * 3σ异常检测：标记超出3倍标准差的记录为异常
     */
    public void markAnomalies(List<SalesHistory> data) {
        double avg = data.stream().mapToDouble(s -> s.getSalesVolume().doubleValue()).average().orElse(0);
        double variance = data.stream()
                .mapToDouble(s -> Math.pow(s.getSalesVolume().doubleValue() - avg, 2))
                .average().orElse(0);
        double std = Math.sqrt(variance);
        double threshold = 3 * std;

        for (SalesHistory sh : data) {
            if (Math.abs(sh.getSalesVolume().doubleValue() - avg) > threshold) {
                sh.setIsAnomaly(1);
                log.debug("异常数据标记: station={}, material={}, date={}, volume={}",
                        sh.getStationId(), sh.getMaterialId(), sh.getSaleDate(), sh.getSalesVolume());
            }
        }
    }

    /**
     * 从 SalesHistory 列表提取数值列表（排除异常值）
     */
        /**
     * 从SalesHistory列表提取数值型销量列表(可排除异常值)
     */
    public List<Double> toValueList(List<SalesHistory> data, boolean excludeAnomaly) {
        return data.stream()
                .filter(s -> !excludeAnomaly || s.getIsAnomaly() != 1)
                .map(s -> s.getSalesVolume().doubleValue())
                .collect(Collectors.toList());
    }

    /**
     * 计算均值
     */
    public double calcMean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    /**
     * 计算标准差
     */
    public double calcStd(List<Double> values) {
        double mean = calcMean(values);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average().orElse(0);
        return Math.sqrt(variance);
    }
}
