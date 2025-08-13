package com.youlai.boot.system.controller;

import com.youlai.boot.system.service.wxUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youlai.boot.system.model.form.wxUserForm;
import com.youlai.boot.system.model.query.wxUserQuery;
import com.youlai.boot.system.model.vo.wxUserVO;
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
 * 用户前端控制层
 *
 * @author liuzijun
 * @since 2025-08-13 15:03
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping("/api/v1/wxuser")
@RequiredArgsConstructor
public class wxUserController  {

    private final wxUserService wxUserService;

    @Operation(summary = "用户分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('system:wxuser:query')")
    public PageResult<wxUserVO> getwxUserPage(wxUserQuery queryParams ) {
        IPage<wxUserVO> result = wxUserService.getwxUserPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('system:wxuser:add')")
    public Result<Void> savewxUser(@RequestBody @Valid wxUserForm formData ) {
        boolean result = wxUserService.savewxUser(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取用户表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('system:wxuser:edit')")
    public Result<wxUserForm> getwxUserForm(
        @Parameter(description = "用户ID") @PathVariable Long id
    ) {
        wxUserForm formData = wxUserService.getwxUserFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "修改用户")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:wxuser:edit')")
    public Result<Void> updatewxUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody @Validated wxUserForm formData
    ) {
        boolean result = wxUserService.updatewxUser(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:wxuser:delete')")
    public Result<Void> deletewxUsers(
        @Parameter(description = "用户ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = wxUserService.deletewxUsers(ids);
        return Result.judge(result);
    }
}