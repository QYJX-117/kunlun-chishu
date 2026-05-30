/**
 * 预测准确率VO —— 模型名称+MAPE+样本数
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
public class ForecastAccuracyVO {
    private String modelName;
    private BigDecimal mape;
    private Long count;
}
