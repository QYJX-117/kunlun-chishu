/**
 * 设备管理实体(预留) —— 对应 equipment 表
 * 油库和加油站设备台账，参考数字孪生体描述
 */

package com.kunlun.kunlun_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("equipment")
public class Equipment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer stationId;
    private String name;
    private String equipmentType;
    private String model;
    private LocalDate installDate;
    private String status;
    private LocalDate lastMaintainDate;
    private LocalDate nextMaintainDate;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
