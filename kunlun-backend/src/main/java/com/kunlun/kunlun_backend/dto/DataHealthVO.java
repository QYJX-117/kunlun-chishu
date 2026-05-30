/**
 * 数据健康度VO —— 完整性/及时性/异常数/准确率
 */

package com.kunlun.kunlun_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataHealthVO {
    private BigDecimal completeness;
    private LocalDate latestDate;
    private Long anomalyCount;
    private BigDecimal accuracyRate;
}
