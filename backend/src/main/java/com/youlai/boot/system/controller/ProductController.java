package com.youlai.boot.system.controller;

import com.youlai.boot.system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youlai.boot.system.model.form.ProductForm;
import com.youlai.boot.system.model.query.ProductQuery;
import com.youlai.boot.system.model.vo.ProductVO;
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
 * 商品表前端控制层
 *
 * @author youlaitech
 * @since 2025-08-08 11:28
 */
@Tag(name = "商品表接口")
@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController  {

    private final ProductService productService;

    @Operation(summary = "商品表分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('system:product:query')")
    public PageResult<ProductVO> getProductPage(ProductQuery queryParams ) {
        IPage<ProductVO> result = productService.getProductPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增商品表")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('system:product:add')")
    public Result<Void> saveProduct(@RequestBody @Valid ProductForm formData ) {
        boolean result = productService.saveProduct(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取商品表表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('system:product:edit')")
    public Result<ProductForm> getProductForm(
            @Parameter(description = "商品表ID") @PathVariable Long id
    ) {
        ProductForm formData = productService.getProductFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "修改商品表")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:product:edit')")
    public Result<Void> updateProduct(
            @Parameter(description = "商品表ID") @PathVariable Long id,
            @RequestBody @Validated ProductForm formData
    ) {
        boolean result = productService.updateProduct(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "删除商品表")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:product:delete')")
    public Result<Void> deleteProducts(
            @Parameter(description = "商品表ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = productService.deleteProducts(ids);
        return Result.judge(result);
    }
}
