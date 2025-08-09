package com.youlai.boot.system.controller;

import com.youlai.boot.system.service.IdTagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youlai.boot.system.model.form.IdTagsForm;
import com.youlai.boot.system.model.query.IdTagsQuery;
import com.youlai.boot.system.model.vo.IdTagsVO;
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
import java.util.List;

/**
 * 标签关联表前端控制层
 *
 * @author liuzijun
 * @since 2025-08-08 12:20
 */
@Tag(name = "标签关联表接口")
@RestController
@RequestMapping("/api/v1/id-tags")
@RequiredArgsConstructor
public class IdTagsController  {

    private final IdTagsService idTagsService;

    @Operation(summary = "标签关联表分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('system:id-tags:query')")
    public PageResult<IdTagsVO> getIdTagsPage(IdTagsQuery queryParams ) {
        IPage<IdTagsVO> result = idTagsService.getIdTagsPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增标签关联表")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('system:id-tags:add')")
    public Result<Void> saveIdTags(@RequestBody @Valid IdTagsForm formData ) {
        boolean result = idTagsService.saveIdTags(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取标签关联表表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('system:id-tags:edit')")
    public Result<IdTagsForm> getIdTagsForm(
            @Parameter(description = "标签关联表ID") @PathVariable Long id
    ) {
        IdTagsForm formData = idTagsService.getIdTagsFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "修改标签关联表")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:id-tags:edit')")
    public Result<Void> updateIdTags(
            @Parameter(description = "标签关联表ID") @PathVariable Long id,
            @RequestBody @Validated IdTagsForm formData
    ) {
        boolean result = idTagsService.updateIdTags(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "删除标签关联表")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:id-tags:delete')")
    public Result<Void> deleteIdTagss(
            @Parameter(description = "标签关联表ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = idTagsService.deleteIdTagss(ids);
        return Result.judge(result);
    }

    @Operation(summary = "根据商品ID删除所有标签关联")
    @DeleteMapping("/product/{productId}")
    @PreAuthorize("@ss.hasPerm('system:id-tags:delete')")
    public Result<Void> deleteByProductId(
            @Parameter(description = "商品ID") @PathVariable Integer productId
    ) {
        boolean result = idTagsService.deleteByProductId(productId);
        return Result.judge(result);
    }

    @Operation(summary = "根据标签ID删除所有商品关联")
    @DeleteMapping("/tag/{tagId}")
    @PreAuthorize("@ss.hasPerm('system:id-tags:delete')")
    public Result<Void> deleteByTagId(
            @Parameter(description = "标签ID") @PathVariable Integer tagId
    ) {
        boolean result = idTagsService.deleteByTagId(tagId);
        return Result.judge(result);
    }

    @Operation(summary = "根据商品ID获取关联的标签ID列表")
    @GetMapping("/product/{productId}/tags")
    @PreAuthorize("@ss.hasPerm('system:id-tags:query')")
    public Result<List<Integer>> getTagIdsByProductId(
            @Parameter(description = "商品ID") @PathVariable Integer productId
    ) {
        List<Integer> result = idTagsService.getTagIdsByProductId(productId);
        return Result.success(result);
    }

    @Operation(summary = "根据标签ID获取关联的商品ID列表")
    @GetMapping("/tag/{tagId}/products")
    @PreAuthorize("@ss.hasPerm('system:id-tags:query')")
    public Result<List<Integer>> getProductIdsByTagId(
            @Parameter(description = "标签ID") @PathVariable Integer tagId
    ) {
        List<Integer> result = idTagsService.getProductIdsByTagId(tagId);
        return Result.success(result);
    }
}
