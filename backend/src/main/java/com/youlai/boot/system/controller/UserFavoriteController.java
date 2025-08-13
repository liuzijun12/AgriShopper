package com.youlai.boot.system.controller;

import com.youlai.boot.system.service.UserFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youlai.boot.system.model.form.UserFavoriteForm;
import com.youlai.boot.system.model.query.UserFavoriteQuery;
import com.youlai.boot.system.model.vo.UserFavoriteVO;
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
 * 收藏前端控制层
 *
 * @author liuzijun
 * @since 2025-08-13 20:03
 */
@Tag(name = "收藏接口")
@RestController
@RequestMapping("/api/v1/userFavorite")
@RequiredArgsConstructor
public class UserFavoriteController  {

    private final UserFavoriteService userFavoriteService;

    @Operation(summary = "收藏分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('system:userFavorite:query')")
    public PageResult<UserFavoriteVO> getUserFavoritePage(UserFavoriteQuery queryParams ) {
        IPage<UserFavoriteVO> result = userFavoriteService.getUserFavoritePage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增收藏")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('system:userFavorite:add')")
    public Result<Void> saveUserFavorite(@RequestBody @Valid UserFavoriteForm formData ) {
        boolean result = userFavoriteService.saveUserFavorite(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取收藏表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('system:userFavorite:edit')")
    public Result<UserFavoriteForm> getUserFavoriteForm(
        @Parameter(description = "收藏ID") @PathVariable Long id
    ) {
        UserFavoriteForm formData = userFavoriteService.getUserFavoriteFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "修改收藏")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:userFavorite:edit')")
    public Result<Void> updateUserFavorite(
            @Parameter(description = "收藏ID") @PathVariable Long id,
            @RequestBody @Validated UserFavoriteForm formData
    ) {
        boolean result = userFavoriteService.updateUserFavorite(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "删除收藏")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:userFavorite:delete')")
    public Result<Void> deleteUserFavorites(
        @Parameter(description = "收藏ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = userFavoriteService.deleteUserFavorites(ids);
        return Result.judge(result);
    }
}
