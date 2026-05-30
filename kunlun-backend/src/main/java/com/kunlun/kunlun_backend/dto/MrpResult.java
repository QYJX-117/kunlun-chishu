/**
 * MRP运行结果DTO —— 含预测值、安全库存、净需求、订单信息、预测方法
 */

package com.kunlun.kunlun_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MrpResult {
    private String stationName;
    private String materialName;
    private BigDecimal currentStock;
    private BigDecimal deadStock;
    private BigDecimal onOrderStock;
    private BigDecimal safetyStock;
    private BigDecimal forecastValue;
    private BigDecimal netRequirement;
    private String predictionMethod;
    private String predictionParams;
    private BigDecimal mse;
    private Boolean orderGenerated;
    private Integer orderId;
    private BigDecimal suggestedQuantity;
    private String requiredDate;
}
