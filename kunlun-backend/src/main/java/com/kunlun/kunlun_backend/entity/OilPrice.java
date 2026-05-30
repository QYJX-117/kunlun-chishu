/**
 * 油价历史实体 —— 对应 oil_price 表
 * 记录历次油价调整，为价格弹性分析提供数据
 */

package com.kunlun.kunlun_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("oil_price")
public class OilPrice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer materialId;
    private LocalDate priceDate;
    private BigDecimal pricePerLiter;
    private BigDecimal adjustment;
    private String source;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
