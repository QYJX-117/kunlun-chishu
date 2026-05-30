/**
 * BI看板控制器
 * GET /api/bi/station-status     —— 所有站点库存状态(含经纬度，用于地图展示)
 * GET /api/bi/station/{id}/recent-sales —— 某站点近N天销量趋势
 * GET /api/bi/data-health        —— 数据健康度(完整性/及时性/异常数)
 * GET /api/bi/forecast-accuracy  —— 各模型预测准确率对比
 * GET /api/bi/inventory-compare  —— 各站点库存对比数据
 */

package com.kunlun.kunlun_backend.controller;

import com.kunlun.kunlun_backend.common.Result;
import com.kunlun.kunlun_backend.dto.*;
import com.kunlun.kunlun_backend.service.bi.BiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bi")
@RequiredArgsConstructor
public class BiController {

    private final BiService biService;

    @GetMapping("/station-status")
    public Result<List<StationStatusVO>> getStationStatus() {
        return Result.ok(biService.getStationStatus());
    }

    @GetMapping("/station/{id}/recent-sales")
    public Result<List<SalesTrendVO>> getRecentSales(@PathVariable Integer id,
                                                      @RequestParam(defaultValue = "7") int days) {
        return Result.ok(biService.getRecentSales(id, days));
    }

    @GetMapping("/data-health")
    public Result<DataHealthVO> getDataHealth() {
        return Result.ok(biService.getDataHealth());
    }

    @GetMapping("/forecast-accuracy")
    public Result<List<ForecastAccuracyVO>> getForecastAccuracy() {
        return Result.ok(biService.getForecastAccuracy());
    }

    @GetMapping("/inventory-compare")
    public Result<List<InventoryCompareVO>> getInventoryCompare() {
        return Result.ok(biService.getInventoryCompare());
    }
}
