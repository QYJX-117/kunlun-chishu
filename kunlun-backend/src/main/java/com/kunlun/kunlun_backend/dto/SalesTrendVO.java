/**
 * 销量趋势VO —— 日期+油品+销量
 */

package com.kunlun.kunlun_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesTrendVO {
    private LocalDate saleDate;
    private String materialName;
    private BigDecimal salesVolume;
}
