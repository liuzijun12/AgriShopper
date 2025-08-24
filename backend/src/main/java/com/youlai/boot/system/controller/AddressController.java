package com.youlai.boot.system.controller;

import com.youlai.boot.system.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youlai.boot.system.model.form.AddressForm;
import com.youlai.boot.system.model.query.AddressQuery;
import com.youlai.boot.system.model.vo.AddressVO;
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
 * 存储用户收货地址信息前端控制层
 *
 * @author youlaitech
 * @since 2025-08-16 00:52
 */
@Tag(name = "存储用户收货地址信息接口")
@RestController
@RequestMapping("/api/v1/address")
@RequiredArgsConstructor
public class AddressController  {

    private final AddressService addressService;

    @Operation(summary = "存储用户收货地址信息分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('system:address:query')")
    public PageResult<AddressVO> getAddressPage(AddressQuery queryParams ) {
        IPage<AddressVO> result = addressService.getAddressPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增存储用户收货地址信息")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('system:address:add')")
    public Result<Void> saveAddress(@RequestBody @Valid AddressForm formData ) {
        boolean result = addressService.saveAddress(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取存储用户收货地址信息表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('system:address:edit')")
    public Result<AddressForm> getAddressForm(
        @Parameter(description = "存储用户收货地址信息ID") @PathVariable Long id
    ) {
        AddressForm formData = addressService.getAddressFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "修改存储用户收货地址信息")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:address:edit')")
    public Result<Void> updateAddress(
            @Parameter(description = "存储用户收货地址信息ID") @PathVariable Long id,
            @RequestBody @Validated AddressForm formData
    ) {
        boolean result = addressService.updateAddress(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "删除存储用户收货地址信息")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:address:delete')")
    public Result<Void> deleteAddresss(
        @Parameter(description = "存储用户收货地址信息ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = addressService.deleteAddresss(ids);
        return Result.judge(result);
    }
}
