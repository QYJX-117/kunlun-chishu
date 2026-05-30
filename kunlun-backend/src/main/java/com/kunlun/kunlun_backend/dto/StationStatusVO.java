/**
 * 站点库存状态VO(用于地图展示) —— 含经纬度+库存+健康状态
 */

package com.kunlun.kunlun_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationStatusVO {
    private Integer stationId;
    private String name;
    private String area;
    private BigDecimal lng;
    private BigDecimal lat;
    private String materialName;
    private BigDecimal currentStock;
    private BigDecimal safetyStock;
    private String stockStatus; // SAFE / WARNING / DANGER
}
