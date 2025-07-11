package com.agrishopper.controller;

import com.agrishopper.model.Address;
import com.agrishopper.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Tag(name = "收货地址管理", description = "收货地址管理相关接口，包括地址的增删改查等操作")
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = "创建收货地址", description = "为指定用户创建新的收货地址")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "地址创建成功"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "400", description = "请求参数有误")
    })
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createAddress(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @RequestBody Address address) {
        try {
            Address createdAddress = addressService.createAddress(userId, address);
            return ResponseEntity.ok(createSuccessResponse(createdAddress));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "更新收货地址", description = "更新指定的收货地址信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "地址更新成功"),
        @ApiResponse(responseCode = "404", description = "地址不存在")
    })
    @PutMapping("/{addressId}")
    public ResponseEntity<?> updateAddress(
            @Parameter(description = "地址ID") @PathVariable Long addressId,
            @RequestBody Address address) {
        try {
            Address updatedAddress = addressService.updateAddress(addressId, address);
            return ResponseEntity.ok(createSuccessResponse(updatedAddress));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "删除收货地址", description = "删除指定的收货地址")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "地址删除成功"),
        @ApiResponse(responseCode = "404", description = "地址不存在")
    })
    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> deleteAddress(
            @Parameter(description = "地址ID") @PathVariable Long addressId) {
        try {
            addressService.deleteAddress(addressId);
            return ResponseEntity.ok(createSuccessResponse(null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "获取收货地址详情", description = "获取指定收货地址的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取地址信息"),
        @ApiResponse(responseCode = "404", description = "地址不存在")
    })
    @GetMapping("/{addressId}")
    public ResponseEntity<?> getAddress(
            @Parameter(description = "地址ID") @PathVariable Long addressId) {
        return addressService.getAddressById(addressId)
                .map(address -> ResponseEntity.ok(createSuccessResponse(address)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("地址不存在")));
    }

    @Operation(summary = "获取用户的所有收货地址", description = "获取指定用户的所有收货地址列表")
    @ApiResponse(responseCode = "200", description = "成功获取地址列表")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserAddresses(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            List<Address> addresses = addressService.getAddressesByUserId(userId);
            return ResponseEntity.ok(createSuccessResponse(addresses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "设置默认收货地址", description = "将指定地址设置为用户的默认收货地址")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "设置默认地址成功"),
        @ApiResponse(responseCode = "404", description = "地址不存在"),
        @ApiResponse(responseCode = "400", description = "该地址不属于当前用户")
    })
    @PutMapping("/{addressId}/default")
    public ResponseEntity<?> setDefaultAddress(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "地址ID") @PathVariable Long addressId) {
        try {
            Address address = addressService.setDefaultAddress(userId, addressId);
            return ResponseEntity.ok(createSuccessResponse(address));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "获取默认收货地址", description = "获取指定用户的默认收货地址")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取默认地址"),
        @ApiResponse(responseCode = "404", description = "未设置默认地址")
    })
    @GetMapping("/user/{userId}/default")
    public ResponseEntity<?> getDefaultAddress(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return addressService.getDefaultAddress(userId)
                .map(address -> ResponseEntity.ok(createSuccessResponse(address)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("未设置默认地址")));
    }

    private Map<String, Object> createSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        
        if (data instanceof Address) {
            Map<String, Object> addressMap = new HashMap<>();
            Address address = (Address) data;
            addressMap.put("id", address.getId());
            addressMap.put("receiverName", address.getReceiverName());
            addressMap.put("receiverPhone", address.getReceiverPhone());
            addressMap.put("province", address.getProvince());
            addressMap.put("city", address.getCity());
            addressMap.put("district", address.getDistrict());
            addressMap.put("detailAddress", address.getDetailAddress());
            addressMap.put("isDefault", address.getIsDefault());
            addressMap.put("userId", address.getUser().getId());
            response.put("data", addressMap);
        } else if (data instanceof List<?> && !((List<?>) data).isEmpty() && ((List<?>) data).get(0) instanceof Address) {
            List<Address> addresses = (List<Address>) data;
            List<Map<String, Object>> addressMaps = addresses.stream().map(address -> {
                Map<String, Object> addressMap = new HashMap<>();
                addressMap.put("id", address.getId());
                addressMap.put("receiverName", address.getReceiverName());
                addressMap.put("receiverPhone", address.getReceiverPhone());
                addressMap.put("province", address.getProvince());
                addressMap.put("city", address.getCity());
                addressMap.put("district", address.getDistrict());
                addressMap.put("detailAddress", address.getDetailAddress());
                addressMap.put("isDefault", address.getIsDefault());
                addressMap.put("userId", address.getUser().getId());
                return addressMap;
            }).toList();
            response.put("data", addressMaps);
        } else {
            response.put("data", data);
        }
        
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        response.put("data", null);
        return response;
    }
} 