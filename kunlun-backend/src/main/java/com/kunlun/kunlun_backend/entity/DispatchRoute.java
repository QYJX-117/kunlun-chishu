/**
 * 调度路线实体(预留) —— 对应 dispatch_route 表
 * 未来实现基于节约算法/遗传算法的路径优化
 */

package com.kunlun.kunlun_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dispatch_route")
public class DispatchRoute {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String routeName;
    private Integer departureId;
    private Integer destinationId;
    private BigDecimal distanceKm;
    private BigDecimal estimatedTimeH;
    private String vehicleType;
    private BigDecimal maxLoadTon;
    private BigDecimal transportCostPerTon;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
