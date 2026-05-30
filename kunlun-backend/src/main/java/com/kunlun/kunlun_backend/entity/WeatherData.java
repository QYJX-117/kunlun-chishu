/**
 * 天气数据实体 —— 对应 weather_data 表
 * 为多特征预测模型提供天气/节假日输入特征
 */

package com.kunlun.kunlun_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("weather_data")
public class WeatherData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String area;
    private LocalDate recordDate;
    private String weatherType;
    private BigDecimal temperatureHigh;
    private BigDecimal temperatureLow;
    private BigDecimal rainfall;
    private Integer isHoliday;
    private String holidayName;
    private BigDecimal holidayFactor;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
