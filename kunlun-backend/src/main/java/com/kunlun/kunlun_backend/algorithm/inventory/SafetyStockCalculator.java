/**
 * 安全库存计算器
 * 完整工业级公式：Is = h × √(T×σo² + Oa²×σT²)
 * 同时考虑销量波动(σo)和到货期波动(σT)
 * 库存状态三级判定：2倍以上=SAFE, 1-2倍=WARNING, 不足=DANGER
 * 参考：马开良、韩玉琴(2024)车用能源储运销技术
 */

package com.kunlun.kunlun_backend.algorithm.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 安全库存计算器
 * 完整工业级公式: Is = h × √(T × σo² + Oa² × σT²)
 * 参考：马开良、韩玉琴(2024)《合理库存模型在成品油供应链中的应用实践》
 */
public class SafetyStockCalculator {

    /**
     * 根据服务水平获取安全系数h
     */
    public static double getSafetyFactor(double serviceLevel) {
        if (serviceLevel >= 0.999) return 3.09;
        if (serviceLevel >= 0.99) return 2.326;
        if (serviceLevel >= 0.95) return 1.645;
        if (serviceLevel >= 0.90) return 1.282;
        return 1.645;
    }

    /**
     * 日安全库存(完整工业级公式)
     * Is = h × √(T × σo² + Oa² × σT²)
     *
     * @param serviceLevel 服务水平 (0.95→h=1.645)
     * @param leadTime     平均到货期T(天)
     * @param stdDemand    日出库量标准差σo
     * @param avgDemand    日均出库量Oa
     * @param stdLeadTime  到货期标准差σT
     */
    public static BigDecimal calcDailySafetyStock(double serviceLevel, double leadTime,
                                                   double stdDemand, double avgDemand,
                                                   double stdLeadTime) {
        double h = getSafetyFactor(serviceLevel);
        double variance = leadTime * stdDemand * stdDemand + avgDemand * avgDemand * stdLeadTime * stdLeadTime;
        double result = h * Math.sqrt(Math.max(0, variance));
        return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 简化版安全库存 SS = Z × σ × √L (用于对比)
     */
    public static BigDecimal calcSimpleSafetyStock(double serviceLevel, double stdDemand, double leadTime) {
        double h = getSafetyFactor(serviceLevel);
        double result = h * stdDemand * Math.sqrt(leadTime);
        return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 月度安全库存 = h × σ_月预测
     */
    public static BigDecimal calcMonthlySafetyStock(double serviceLevel, double stdMonthlyForecast) {
        double h = getSafetyFactor(serviceLevel);
        double result = h * stdMonthlyForecast;
        return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 净需求计算
     * Net = Forecast - CurrentStock - OnOrderStock + SafetyStock + DeadStock
     */
    public static BigDecimal calcNetRequirement(BigDecimal forecast, BigDecimal currentStock,
                                                 BigDecimal onOrderStock, BigDecimal safetyStock,
                                                 BigDecimal deadStock) {
        BigDecimal result = forecast
                .subtract(currentStock)
                .subtract(onOrderStock != null ? onOrderStock : BigDecimal.ZERO)
                .add(safetyStock)
                .add(deadStock != null ? deadStock : BigDecimal.ZERO);
        return result.compareTo(BigDecimal.ZERO) > 0 ? result : BigDecimal.ZERO;
    }

    /**
     * 库存状态判定：SAFE/WARNING/DANGER
     */
    public static String getStockStatus(BigDecimal currentStock, BigDecimal safetyStock) {
        if (currentStock.compareTo(safetyStock.multiply(new BigDecimal("2"))) >= 0) {
            return "SAFE";
        } else if (currentStock.compareTo(safetyStock) >= 0) {
            return "WARNING";
        }
        return "DANGER";
    }
}
