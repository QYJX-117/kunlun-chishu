/**
 * 站点库存实体 —— 对应 station_inventory 表
 * 记录当前库存、不可动库存(库底油)、在途库存、安全库存
 * 含乐观锁 version 字段，防止高并发更新冲突
 */

package com.kunlun.kunlun_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("station_inventory")
public class StationInventory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer stationId;
    private Integer materialId;
    private BigDecimal currentStock;
    private BigDecimal deadStock;
    private BigDecimal onOrderStock;
    private BigDecimal safetyStock;
    private BigDecimal tankCapacity;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastUpdated;
    @Version
    private Integer version;
}
