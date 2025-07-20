package com.agrishopper.controller;

import com.agrishopper.common.ApiResponse;
import com.agrishopper.model.UserAddress;
import com.agrishopper.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户地址管理控制器
 */
@RestController
@RequestMapping("/api/address")
@Tag(name = "用户地址管理", description = "用户地址的增删改查接口")
public class UserAddressController {
    
    @Autowired
    private UserAddressService userAddressService;
    
    /**
     * 获取用户的所有地址列表
     * GET /api/address/list
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户地址列表", description = "获取当前用户的所有收货地址，默认地址排在前面")
    public ApiResponse<List<UserAddress>> getUserAddresses(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            List<UserAddress> addresses = userAddressService.getUserAddresses(userId);
            return ApiResponse.success(addresses);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取地址列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户的默认地址
     * GET /api/address/default
     */
    @GetMapping("/default")
    @Operation(summary = "获取默认地址", description = "获取当前用户的默认收货地址")
    public ApiResponse<UserAddress> getDefaultAddress(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            UserAddress defaultAddress = userAddressService.getDefaultAddress(userId);
            if (defaultAddress != null) {
                return ApiResponse.success(defaultAddress);
            } else {
                return ApiResponse.error(404, "暂无默认地址");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "获取默认地址失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取地址详情
     * GET /api/address/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取地址详情", description = "根据地址ID获取地址详细信息")
    public ApiResponse<UserAddress> getAddressById(
            @Parameter(description = "地址ID") @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            UserAddress address = userAddressService.getAddressById(id, userId);
            if (address != null) {
                return ApiResponse.success(address);
            } else {
                return ApiResponse.error(404, "地址不存在或无权限访问");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "获取地址详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 新增地址
     * POST /api/address/add
     */
    @PostMapping("/add")
    @Operation(summary = "新增地址", description = "添加新的收货地址")
    public ApiResponse<UserAddress> addAddress(
            @Parameter(description = "地址信息") @RequestBody UserAddress userAddress) {
        try {
            UserAddress newAddress = userAddressService.addAddress(userAddress);
            return ApiResponse.success(newAddress);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "新增地址失败：" + e.getMessage());
        }
    }
    
    /**
     * 修改地址
     * PUT /api/address/update
     */
    @PutMapping("/update")
    @Operation(summary = "修改地址", description = "修改已有的收货地址信息")
    public ApiResponse<UserAddress> updateAddress(
            @Parameter(description = "地址信息") @RequestBody UserAddress userAddress) {
        try {
            UserAddress updatedAddress = userAddressService.updateAddress(userAddress);
            return ApiResponse.success(updatedAddress);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "修改地址失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除地址
     * DELETE /api/address/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除地址", description = "删除指定的收货地址（软删除）")
    public ApiResponse<Boolean> deleteAddress(
            @Parameter(description = "地址ID") @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            boolean success = userAddressService.deleteAddress(id, userId);
            if (success) {
                return ApiResponse.success(true);
            } else {
                return ApiResponse.error(404, "地址不存在或无权限删除");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "删除地址失败：" + e.getMessage());
        }
    }
    
    /**
     * 设置默认地址
     * PUT /api/address/set-default/{id}
     */
    @PutMapping("/set-default/{id}")
    @Operation(summary = "设置默认地址", description = "将指定地址设置为默认收货地址")
    public ApiResponse<Boolean> setDefaultAddress(
            @Parameter(description = "地址ID") @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            boolean success = userAddressService.setDefaultAddress(id, userId);
            if (success) {
                return ApiResponse.success(true);
            } else {
                return ApiResponse.error(404, "地址不存在或无权限设置");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "设置默认地址失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户地址数量
     * GET /api/address/count
     */
    @GetMapping("/count")
    @Operation(summary = "获取地址数量", description = "获取当前用户的地址数量")
    public ApiResponse<Long> getAddressCount(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            long count = userAddressService.getAddressCount(userId);
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取地址数量失败：" + e.getMessage());
        }
    }
    
    /**
     * 验证地址信息
     * POST /api/address/validate
     */
    @PostMapping("/validate")
    @Operation(summary = "验证地址信息", description = "验证地址信息的完整性和有效性")
    public ApiResponse<Boolean> validateAddress(
            @Parameter(description = "地址信息") @RequestBody UserAddress userAddress) {
        try {
            boolean isValid = userAddressService.validateAddress(userAddress);
            if (isValid) {
                return ApiResponse.success(true);
            } else {
                return ApiResponse.error(400, "地址信息验证失败，请检查必填字段和格式");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "验证地址信息失败：" + e.getMessage());
        }
    }
} 