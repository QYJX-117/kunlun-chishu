/**
 * 经济订货批量 EOQ = √(2DS/H)
 * 平衡订货成本与持有成本的理论最优批量
 * 参考：马开良、韩玉琴(2024)
 */

package com.kunlun.kunlun_backend.algorithm.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 经济订货批量 EOQ = √(2DS/H)
 * 参考：马开良、韩玉琴(2024)
 */
public class EOQCalculator {

    /**
     * @param annualDemand 年需求量
     * @param orderCost    单次订货成本
     * @param holdingCost  单位年持有成本
     * @return 经济订货批量
     */
    public static BigDecimal calcEOQ(double annualDemand, double orderCost, double holdingCost) {
        if (holdingCost <= 0) return BigDecimal.ZERO;
        double result = Math.sqrt(2 * annualDemand * orderCost / holdingCost);
        return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
    }
}
