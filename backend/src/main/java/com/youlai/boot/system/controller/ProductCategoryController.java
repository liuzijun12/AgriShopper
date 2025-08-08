package com.youlai.boot.system.controller;

import com.youlai.boot.system.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youlai.boot.system.model.form.ProductCategoryForm;
import com.youlai.boot.system.model.query.ProductCategoryQuery;
import com.youlai.boot.system.model.vo.ProductCategoryVO;
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
 * 分类前端控制层
 *
 * @author liuzijun
 * @since 2025-08-08 12:02
 */
@Tag(name = "分类接口")
@RestController
@RequestMapping("/api/v1/product-category")
@RequiredArgsConstructor
public class ProductCategoryController  {

    private final ProductCategoryService productCategoryService;

    @Operation(summary = "分类分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('system:product-category:query')")
    public PageResult<ProductCategoryVO> getProductCategoryPage(ProductCategoryQuery queryParams ) {
        IPage<ProductCategoryVO> result = productCategoryService.getProductCategoryPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增分类")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('system:product-category:add')")
    public Result<Void> saveProductCategory(@RequestBody @Valid ProductCategoryForm formData ) {
        boolean result = productCategoryService.saveProductCategory(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取分类表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('system:product-category:edit')")
    public Result<ProductCategoryForm> getProductCategoryForm(
            @Parameter(description = "分类ID") @PathVariable Long id
    ) {
        ProductCategoryForm formData = productCategoryService.getProductCategoryFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "修改分类")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:product-category:edit')")
    public Result<Void> updateProductCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @RequestBody @Validated ProductCategoryForm formData
    ) {
        boolean result = productCategoryService.updateProductCategory(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:product-category:delete')")
    public Result<Void> deleteProductCategorys(
            @Parameter(description = "分类ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = productCategoryService.deleteProductCategorys(ids);
        return Result.judge(result);
    }
}
