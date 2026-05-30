/**
 * MRP运行请求DTO —— 站点ID+油品ID+预测天数
 */

package com.kunlun.kunlun_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MrpRequest {
    @NotNull(message = "站点ID不能为空")
    private Integer stationId;
    @NotNull(message = "油品ID不能为空")
    private Integer materialId;
    private Integer forecastDays = 3;
}
