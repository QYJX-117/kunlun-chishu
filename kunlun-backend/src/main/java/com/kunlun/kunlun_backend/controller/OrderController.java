/**
 * 订单管理控制器
 * GET  /api/orders          —— 分页查询订单(支持status筛选)
 * PUT  /api/orders/{id}/confirm  —— 确认订单(更新在途库存)
 * PUT  /api/orders/{id}/complete —— 完成订单(到货入库)
 * PUT  /api/orders/{id}/cancel   —— 取消订单
 */

package com.kunlun.kunlun_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kunlun.kunlun_backend.common.Result;
import com.kunlun.kunlun_backend.entity.PlannedOrder;
import com.kunlun.kunlun_backend.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Result<IPage<PlannedOrder>> page(@RequestParam(required = false) String status,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        return Result.ok(orderService.page(status, page, size));
    }

    @PutMapping("/{id}/confirm")
    public Result<?> confirm(@PathVariable Integer id, @RequestParam(defaultValue = "0") Integer userId) {
        orderService.confirmOrder(id, userId);
        return Result.ok();
    }

    @PutMapping("/{id}/complete")
    public Result<?> complete(@PathVariable Integer id) {
        orderService.completeOrder(id);
        return Result.ok();
    }

    @PutMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Integer id) {
        orderService.cancelOrder(id);
        return Result.ok();
    }
}
