/**
 * 补货订单实体 —— 对应 planned_order 表
 * MRP引擎的输出结果，记录补货建议的完整决策链路
 * 含预测方法、参数、安全库存值等审计字段
 */

package com.kunlun.kunlun_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("planned_order")
public class PlannedOrder {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer stationId;
    private Integer materialId;
    private BigDecimal suggestedQuantity;
    private LocalDate requiredDate;
    private String status;
    private String predictionMethod;
    private String predictionParams;
    private BigDecimal safetyStockUsed;
    private BigDecimal netRequirement;
    private BigDecimal forecastValue;
    private String generatedBy;
    private Integer confirmedBy;
    private LocalDateTime confirmedAt;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
