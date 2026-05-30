/**
 * 油品信息实体 —— 对应 material 表
 * 定义92#/95#/98#汽油、0#/-10#柴油的物理属性与到货期参数
 */

package com.kunlun.kunlun_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("material")
public class Material {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String name;
    private BigDecimal density;
    private String unit;
    private String type;
    private BigDecimal leadTime;
    private BigDecimal leadTimeStd;
    private BigDecimal pricePerLiter;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
