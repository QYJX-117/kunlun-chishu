/**
 * 调度管理控制器(预留) —— 当前返回模拟数据
 * 未来实现：基于节约算法/遗传算法的路径优化
 */

package com.kunlun.kunlun_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.kunlun_backend.common.Result;
import com.kunlun.kunlun_backend.entity.DispatchRoute;
import com.kunlun.kunlun_backend.mapper.DispatchRouteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调度管理（预留扩展）
 * 当前返回模拟数据，未来实现基于节约算法/遗传算法的路径优化
 */
@RestController
@RequestMapping("/api/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchRouteMapper dispatchRouteMapper;

    @GetMapping("/routes")
    public Result<IPage<DispatchRoute>> routes(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        return Result.ok(dispatchRouteMapper.selectPage(new Page<>(page, size), null));
    }
}
