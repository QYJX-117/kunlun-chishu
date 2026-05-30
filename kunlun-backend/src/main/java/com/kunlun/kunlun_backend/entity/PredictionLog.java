/**
 * 预测日志实体 —— 对应 prediction_log 表
 * 系统的黑匣子，记录每次预测的方法、参数、误差指标
 * 支持跟踪信号(TS)监控，|TS|>4时触发模型重校准
 */

package com.kunlun.kunlun_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("prediction_log")
public class PredictionLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer stationId;
    private Integer materialId;
    private LocalDate predictDate;
    private String modelName;
    private String params;
    private BigDecimal forecastValue;
    private BigDecimal actualValue;
    private BigDecimal mse;
    private BigDecimal mae;
    private BigDecimal mape;
    private BigDecimal trackingSignal;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
