/**
 * 库存对比VO —— 站点+油品+当前库存vs安全库存vs罐容
 */

package com.kunlun.kunlun_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCompareVO {
    private Integer stationId;
    private String stationName;
    private String materialName;
    private BigDecimal currentStock;
    private BigDecimal safetyStock;
    private BigDecimal tankCapacity;
}
