package com.youlai.boot.system.controller;

import com.youlai.boot.system.service.ProductTagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youlai.boot.system.model.form.ProductTagsForm;
import com.youlai.boot.system.model.query.ProductTagsQuery;
import com.youlai.boot.system.model.vo.ProductTagsVO;
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
 * 标签前端控制层
 *
 * @author liuzijun
 * @since 2025-08-08 12:10
 */
@Tag(name = "标签接口")
@RestController
@RequestMapping("/api/v1/product-tags")
@RequiredArgsConstructor
public class ProductTagsController  {

    private final ProductTagsService productTagsService;

    @Operation(summary = "标签分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('system:product-tags:query')")
    public PageResult<ProductTagsVO> getProductTagsPage(ProductTagsQuery queryParams ) {
        IPage<ProductTagsVO> result = productTagsService.getProductTagsPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增标签")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('system:product-tags:add')")
    public Result<Void> saveProductTags(@RequestBody @Valid ProductTagsForm formData ) {
        boolean result = productTagsService.saveProductTags(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取标签表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('system:product-tags:edit')")
    public Result<ProductTagsForm> getProductTagsForm(
            @Parameter(description = "标签ID") @PathVariable Long id
    ) {
        ProductTagsForm formData = productTagsService.getProductTagsFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "修改标签")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:product-tags:edit')")
    public Result<Void> updateProductTags(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @RequestBody @Validated ProductTagsForm formData
    ) {
        boolean result = productTagsService.updateProductTags(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:product-tags:delete')")
    public Result<Void> deleteProductTagss(
            @Parameter(description = "标签ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = productTagsService.deleteProductTagss(ids);
        return Result.judge(result);
    }
}
