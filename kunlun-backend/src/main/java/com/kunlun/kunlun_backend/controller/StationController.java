/**
 * 站点管理控制器 —— CRUD + 分页查询 + 按名称/区域筛选
 */

package com.kunlun.kunlun_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.kunlun_backend.common.Result;
import com.kunlun.kunlun_backend.entity.Station;
import com.kunlun.kunlun_backend.mapper.StationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationMapper stationMapper;

    @GetMapping
    public Result<IPage<Station>> page(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "20") int size,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String area) {
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<Station>()
                .like(StringUtils.hasText(name), Station::getName, name)
                .eq(StringUtils.hasText(area), Station::getArea, area)
                .orderByAsc(Station::getArea, Station::getName);
        return Result.ok(stationMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @GetMapping("/{id}")
    public Result<Station> getById(@PathVariable Integer id) {
        return Result.ok(stationMapper.selectById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody Station station) {
        stationMapper.insert(station);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody Station station) {
        station.setId(id);
        stationMapper.updateById(station);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        stationMapper.deleteById(id);
        return Result.ok();
    }
}
