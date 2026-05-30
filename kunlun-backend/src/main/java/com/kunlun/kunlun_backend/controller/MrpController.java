/**
 * MRP补货引擎控制器
 * POST /api/mrp/run —— 运行MRP补货计算(5步流程：数据清洗→预测→安全库存→净需求→生成订单)
 */

package com.kunlun.kunlun_backend.controller;

import com.kunlun.kunlun_backend.common.Result;
import com.kunlun.kunlun_backend.dto.MrpRequest;
import com.kunlun.kunlun_backend.dto.MrpResult;
import com.kunlun.kunlun_backend.service.mrp.MrpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mrp")
@RequiredArgsConstructor
public class MrpController {

    private final MrpService mrpService;

    @PostMapping("/run")
    public Result<MrpResult> runMrp(@Valid @RequestBody MrpRequest request) {
        MrpResult result = mrpService.runMrp(
                request.getStationId(),
                request.getMaterialId(),
                request.getForecastDays() != null ? request.getForecastDays() : 3);
        return Result.ok(result);
    }
}
