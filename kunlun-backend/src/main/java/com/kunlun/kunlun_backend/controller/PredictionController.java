/**
 * 预测日志控制器 —— 分页查询预测执行记录
 */

package com.kunlun.kunlun_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.kunlun_backend.common.Result;
import com.kunlun.kunlun_backend.entity.PredictionLog;
import com.kunlun.kunlun_backend.mapper.PredictionLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prediction")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionLogMapper predictionLogMapper;

    @GetMapping("/logs")
    public Result<IPage<PredictionLog>> page(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        Page<PredictionLog> p = new Page<>(page, size);
        return Result.ok(predictionLogMapper.selectPage(p,
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PredictionLog>()
                        .orderByDesc(PredictionLog::getPredictDate)));
    }
}
