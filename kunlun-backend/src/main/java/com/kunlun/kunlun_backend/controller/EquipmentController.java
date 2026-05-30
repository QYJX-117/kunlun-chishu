/**
 * 设备管理控制器(预留) —— 当前返回模拟数据
 * 展示油库和加油站设备台账信息
 */

package com.kunlun.kunlun_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.kunlun_backend.common.Result;
import com.kunlun.kunlun_backend.entity.Equipment;
import com.kunlun.kunlun_backend.mapper.EquipmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 设备管理（预留扩展）
 */
@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentMapper equipmentMapper;

    @GetMapping("/list")
    public Result<IPage<Equipment>> list(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        return Result.ok(equipmentMapper.selectPage(new Page<>(page, size), null));
    }
}
