/**
 * 油品管理控制器 —— CRUD + 分页查询 + 按名称/类型筛选
 */

package com.kunlun.kunlun_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.kunlun_backend.common.Result;
import com.kunlun.kunlun_backend.entity.Material;
import com.kunlun.kunlun_backend.mapper.MaterialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialMapper materialMapper;

    @GetMapping
    public Result<IPage<Material>> page(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int size,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false) String type) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<Material>()
                .like(StringUtils.hasText(name), Material::getName, name)
                .eq(StringUtils.hasText(type), Material::getType, type)
                .orderByAsc(Material::getType, Material::getName);
        return Result.ok(materialMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @GetMapping("/{id}")
    public Result<Material> getById(@PathVariable Integer id) {
        return Result.ok(materialMapper.selectById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody Material material) {
        materialMapper.insert(material);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody Material material) {
        material.setId(id);
        materialMapper.updateById(material);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        materialMapper.deleteById(id);
        return Result.ok();
    }
}
