/**
 * 销量历史实体 —— 对应 sales_history 表
 * 数据量最大、最核心的表，承载所有预测模型的训练数据
 * 含 is_anomaly 异常标记字段，支持3σ异常检测
 */

package com.kunlun.kunlun_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sales_history")
public class SalesHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer stationId;
    private Integer materialId;
    private LocalDate saleDate;
    private BigDecimal salesVolume;
    private Integer isAnomaly;
    private String source;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
