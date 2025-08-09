package com.youlai.boot.system.controller;

import com.youlai.boot.system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youlai.boot.system.model.form.ProductForm;
import com.youlai.boot.system.model.query.ProductQuery;
import com.youlai.boot.system.model.vo.ProductVO;
import com.youlai.boot.system.model.vo.TagVO;
import com.youlai.boot.system.model.vo.ProductCategoryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
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

    @Operation(summary = "商品分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('system:product:query')")
    public PageResult<ProductVO> getProductPage(ProductQuery queryParams) {
        IPage<ProductVO> result = productService.getProductPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "根据ID查询商品")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPerm('system:product:query')")
    public Result<ProductVO> getProductById(
            @Parameter(description = "商品ID") @PathVariable Long id
    ) {
        ProductVO product = productService.getProductById(id);
        return Result.success(product);
    }

    @Operation(summary = "新增商品")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('system:product:add')")
    public Result<Void> addProduct(@RequestBody @Valid ProductForm formData) {
        boolean result = productService.saveProduct(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取商品表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('system:product:edit')")
    public Result<ProductForm> getProductFormData(
            @Parameter(description = "商品ID") @PathVariable Long id
    ) {
        ProductForm formData = productService.getProductFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "更新商品")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:product:edit')")
    public Result<Void> updateProduct(
            @Parameter(description = "商品ID") @PathVariable Long id,
            @RequestBody @Validated ProductForm formData
    ) {
        boolean result = productService.updateProduct(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:product:delete')")
    public Result<Void> deleteProduct(
            @Parameter(description = "商品ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = productService.deleteProducts(ids);
        return Result.judge(result);
    }

    @Operation(summary = "获取商品详情（包含标签）")
    @GetMapping("/{id}/with-tags")
    @PreAuthorize("@ss.hasPerm('system:product:query')")
    public Result<ProductVO> getProductWithTags(
            @Parameter(description = "商品ID") @PathVariable Long id
    ) {
        ProductVO product = productService.getProductWithTags(id);
        return Result.success(product);
    }

    @Operation(summary = "获取商品标签列表")
    @GetMapping("/{id}/tags")
    @PreAuthorize("@ss.hasPerm('system:product:query')")
    public Result<List<TagVO>> getProductTags(
            @Parameter(description = "商品ID") @PathVariable Long id
    ) {
        List<TagVO> tags = productService.getTagsByProductId(id);
        return Result.success(tags);
    }

    @Operation(summary = "获取商品详情（包含分类）")
    @GetMapping("/{id}/with-categories")
    @PreAuthorize("@ss.hasPerm('system:product:query')")
    public Result<ProductVO> getProductWithCategories(
            @Parameter(description = "商品ID") @PathVariable Long id
    ) {
        ProductVO product = productService.getProductWithCategories(id);
        return Result.success(product);
    }

    @Operation(summary = "获取商品分类列表")
    @GetMapping("/{id}/categories")
    @PreAuthorize("@ss.hasPerm('system:product:query')")
    public Result<List<ProductCategoryVO>> getProductCategories(
            @Parameter(description = "商品ID") @PathVariable Long id
    ) {
        List<ProductCategoryVO> categories = productService.getCategoriesByProductId(id);
        return Result.success(categories);
    }
}
