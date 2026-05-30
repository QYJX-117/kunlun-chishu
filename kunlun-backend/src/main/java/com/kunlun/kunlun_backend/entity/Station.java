/**
 * 加油站/油库信息实体 —— 对应 station 表
 * 系统核心主数据，记录站点位置、等级、服务水平参数等
 * 设计参考：GB50156-2021国标、马开良(2024)安全库存公式
 */
package com.kunlun.kunlun_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("station")
public class Station {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String name;
    private String area;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String stationType;
    private String grade;
    private BigDecimal serviceLevel;
    private BigDecimal defaultLeadTime;
    private BigDecimal defaultLeadTimeStd;
    private String status;
    private String contactPhone;
    private String address;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
