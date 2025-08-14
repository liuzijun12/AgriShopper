package com.youlai.boot.system.controller;

import com.youlai.boot.system.service.ProductCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youlai.boot.system.model.form.ProductCartForm;
import com.youlai.boot.system.model.query.ProductCartQuery;
import com.youlai.boot.system.model.vo.ProductCartVO;
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
 * 购物车前端控制层
 *
 * @author liuzijun
 * @since 2025-08-13 21:13
 */
@Tag(name = "购物车接口")
@RestController
@RequestMapping("/api/v1/productCart")
@RequiredArgsConstructor
public class ProductCartController  {

    private final ProductCartService productCartService;

    @Operation(summary = "购物车分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('system:productCart:query')")
    public PageResult<ProductCartVO> getProductCartPage(ProductCartQuery queryParams ) {
        IPage<ProductCartVO> result = productCartService.getProductCartPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增购物车")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('system:productCart:add')")
    public Result<Void> saveProductCart(@RequestBody @Valid ProductCartForm formData ) {
        boolean result = productCartService.saveProductCart(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取购物车表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('system:productCart:edit')")
    public Result<ProductCartForm> getProductCartForm(
        @Parameter(description = "购物车ID") @PathVariable Long id
    ) {
        ProductCartForm formData = productCartService.getProductCartFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "修改购物车")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:productCart:edit')")
    public Result<Void> updateProductCart(
            @Parameter(description = "购物车ID") @PathVariable Long id,
            @RequestBody @Validated ProductCartForm formData
    ) {
        boolean result = productCartService.updateProductCart(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "删除购物车")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:productCart:delete')")
    public Result<Void> deleteProductCarts(
        @Parameter(description = "购物车ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = productCartService.deleteProductCarts(ids);
        return Result.judge(result);
    }
}
