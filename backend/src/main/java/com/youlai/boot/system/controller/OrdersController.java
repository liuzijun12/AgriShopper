package com.youlai.boot.system.controller;

import com.youlai.boot.system.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youlai.boot.system.model.form.OrdersForm;
import com.youlai.boot.system.model.query.OrdersQuery;
import com.youlai.boot.system.model.vo.OrdersVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.boot.common.result.PageResult;
import com.youlai.boot.common.result.Result;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * 订单前端控制层
 *
 * @author youlaitech
 * @since 2025-08-16 00:58
 */
@Tag(name = "订单接口")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrdersController  {

    private final OrdersService ordersService;

    @Operation(summary = "订单分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('system:orders:query')")
    public PageResult<OrdersVO> getOrdersPage(OrdersQuery queryParams ) {
        IPage<OrdersVO> result = ordersService.getOrdersPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增订单")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('system:orders:add')")
    public Result<Void> saveOrders(@RequestBody @Valid OrdersForm formData ) {
        boolean result = ordersService.saveOrders(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取订单表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('system:orders:edit')")
    public Result<OrdersForm> getOrdersForm(
        @Parameter(description = "订单ID") @PathVariable Long id
    ) {
        OrdersForm formData = ordersService.getOrdersFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "修改订单")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:orders:edit')")
    public Result<Void> updateOrders(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestBody @Validated OrdersForm formData
    ) {
        boolean result = ordersService.updateOrders(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:orders:delete')")
    public Result<Void> deleteOrderss(
        @Parameter(description = "订单ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = ordersService.deleteOrderss(ids);
        return Result.judge(result);
    }
}
