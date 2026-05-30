/**
 * 订单管理服务
 * 订单状态流转：PENDING → CONFIRMED/COMPLETED/CANCELLED
 * 确认时更新站点在途库存，完成时入库并扣减在途
 * 所有操作在 @Transactional 事务中执行
 */

package com.kunlun.kunlun_backend.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.kunlun_backend.entity.PlannedOrder;
import com.kunlun.kunlun_backend.entity.StationInventory;
import com.kunlun.kunlun_backend.mapper.PlannedOrderMapper;
import com.kunlun.kunlun_backend.mapper.StationInventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final PlannedOrderMapper orderMapper;
    private final StationInventoryMapper inventoryMapper;

    public IPage<PlannedOrder> page(String status, int page, int size) {
        LambdaQueryWrapper<PlannedOrder> wrapper = new LambdaQueryWrapper<PlannedOrder>()
                .eq(status != null && !status.isEmpty(), PlannedOrder::getStatus, status)
                .orderByDesc(PlannedOrder::getCreatedAt);
        return orderMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 确认订单：PENDING→CONFIRMED，同时增加站点在途库存
     */
    @Transactional
    public void confirmOrder(Integer orderId, Integer userId) {
        PlannedOrder order = orderMapper.selectById(orderId);
        if (order == null || !"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许确认操作");
        }
        order.setStatus("CONFIRMED");
        order.setConfirmedBy(userId);
        order.setConfirmedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        // 更新在途库存
        StationInventory inv = inventoryMapper.selectOne(
                new LambdaQueryWrapper<StationInventory>()
                        .eq(StationInventory::getStationId, order.getStationId())
                        .eq(StationInventory::getMaterialId, order.getMaterialId()));
        if (inv != null) {
            BigDecimal onOrder = inv.getOnOrderStock() != null ? inv.getOnOrderStock() : BigDecimal.ZERO;
            inv.setOnOrderStock(onOrder.add(order.getSuggestedQuantity()));
            inventoryMapper.updateById(inv);
        }
    }

    /**
     * 完成订单：CONFIRMED→COMPLETED，扣减在途库存并入库
     */
    @Transactional
    public void completeOrder(Integer orderId) {
        PlannedOrder order = orderMapper.selectById(orderId);
        if (order == null || !"CONFIRMED".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许完成操作");
        }
        order.setStatus("COMPLETED");
        orderMapper.updateById(order);

        // 到货入库
        StationInventory inv = inventoryMapper.selectOne(
                new LambdaQueryWrapper<StationInventory>()
                        .eq(StationInventory::getStationId, order.getStationId())
                        .eq(StationInventory::getMaterialId, order.getMaterialId()));
        if (inv != null) {
            BigDecimal onOrder = inv.getOnOrderStock() != null ? inv.getOnOrderStock() : BigDecimal.ZERO;
            inv.setOnOrderStock(onOrder.subtract(order.getSuggestedQuantity()));
            inv.setCurrentStock(inv.getCurrentStock().add(order.getSuggestedQuantity()));
            inventoryMapper.updateById(inv);
        }
    }

    /**
     * 取消订单：PENDING→CANCELLED
     */
    @Transactional
    public void cancelOrder(Integer orderId) {
        PlannedOrder order = orderMapper.selectById(orderId);
        if (order == null || !"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许取消操作");
        }
        order.setStatus("CANCELLED");
        orderMapper.updateById(order);
    }
}
