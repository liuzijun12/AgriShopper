package com.youlai.boot.system.controller;

import com.youlai.boot.system.service.IdCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youlai.boot.system.model.form.IdCategoryForm;
import com.youlai.boot.system.model.query.IdCategoryQuery;
import com.youlai.boot.system.model.vo.IdCategoryVO;
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
 * 分类关联前端控制层
 *
 * @author liuzijun
 * @since 2025-08-08 12:16
 */
@Tag(name = "分类关联接口")
@RestController
@RequestMapping("/api/v1/id-category")
@RequiredArgsConstructor
public class IdCategoryController  {

    private final IdCategoryService idCategoryService;

    @Operation(summary = "分类关联分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('system:id-category:query')")
    public PageResult<IdCategoryVO> getIdCategoryPage(IdCategoryQuery queryParams ) {
        IPage<IdCategoryVO> result = idCategoryService.getIdCategoryPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增分类关联")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('system:id-category:add')")
    public Result<Void> saveIdCategory(@RequestBody @Valid IdCategoryForm formData ) {
        boolean result = idCategoryService.saveIdCategory(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取分类关联表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('system:id-category:edit')")
    public Result<IdCategoryForm> getIdCategoryForm(
            @Parameter(description = "分类关联ID") @PathVariable Long id
    ) {
        IdCategoryForm formData = idCategoryService.getIdCategoryFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "修改分类关联")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:id-category:edit')")
    public Result<Void> updateIdCategory(
            @Parameter(description = "分类关联ID") @PathVariable Long id,
            @RequestBody @Validated IdCategoryForm formData
    ) {
        boolean result = idCategoryService.updateIdCategory(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "删除分类关联")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:id-category:delete')")
    public Result<Void> deleteIdCategorys(
            @Parameter(description = "分类关联ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = idCategoryService.deleteIdCategorys(ids);
        return Result.judge(result);
    }
}
