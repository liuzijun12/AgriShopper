package com.agrishopper.controller;

import com.agrishopper.common.ApiResponse;
import com.agrishopper.dto.AddToCartRequest;
import com.agrishopper.model.ShoppingCart;
import com.agrishopper.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "购物车管理", description = "购物车相关接口")
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Operation(summary = "添加商品到购物车")
    @PostMapping("/add")
    public ApiResponse<ShoppingCart> addToCart(@RequestBody AddToCartRequest request) {
        try {
            // 验证用户ID是否有效
            if (request.getUserId() == null || request.getUserId() <= 0) {
                return ApiResponse.error(400, "无效的用户ID");
            }
            
            // 验证商品ID是否有效
            if (request.getProductId() == null || request.getProductId() <= 0) {
                return ApiResponse.error(400, "无效的商品ID");
            }
            
            // 验证数量是否有效
            if (request.getQuantity() == null || request.getQuantity() <= 0) {
                return ApiResponse.error(400, "无效的商品数量");
            }
            
            ShoppingCart cart = shoppingCartService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity());
            return ApiResponse.success(cart);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "更新购物车商品数量")
    @PutMapping("/update")
    public ApiResponse<ShoppingCart> updateQuantity(@RequestBody AddToCartRequest request) {
        try {
            ShoppingCart cart = shoppingCartService.updateQuantity(request.getUserId(), request.getProductId(), request.getQuantity());
            return ApiResponse.success(cart);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "从购物车移除商品")
    @DeleteMapping("/remove")
    public ApiResponse<Void> removeFromCart(@RequestBody AddToCartRequest request) {
        try {
            shoppingCartService.removeFromCart(request.getUserId(), request.getProductId());
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "获取用户购物车列表")
    @GetMapping("/list")
    public ApiResponse<List<ShoppingCart>> getUserCart(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            List<ShoppingCart> cartItems = shoppingCartService.getUserCart(userId);
            return ApiResponse.success(cartItems);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "获取用户选中的购物车项")
    @GetMapping("/selected")
    public ApiResponse<List<ShoppingCart>> getSelectedItems(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            List<ShoppingCart> selectedItems = shoppingCartService.getSelectedItems(userId);
            return ApiResponse.success(selectedItems);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "清空用户购物车")
    @DeleteMapping("/clear")
    public ApiResponse<Void> clearCart(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            shoppingCartService.clearCart(userId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "清空用户选中的购物车项")
    @DeleteMapping("/clear-selected")
    public ApiResponse<Void> clearSelectedItems(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            shoppingCartService.clearSelectedItems(userId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "更新购物车项选中状态")
    @PutMapping("/select")
    public ApiResponse<Void> updateSelectedStatus(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "购物车项ID") @RequestParam Long cartItemId,
            @Parameter(description = "是否选中") @RequestParam Boolean isSelected) {
        try {
            shoppingCartService.updateSelectedStatus(userId, cartItemId, isSelected);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "批量更新购物车项选中状态")
    @PutMapping("/batch-select")
    public ApiResponse<Void> batchUpdateSelectedStatus(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "购物车项ID列表") @RequestParam List<Long> cartItemIds,
            @Parameter(description = "是否选中") @RequestParam Boolean isSelected) {
        try {
            shoppingCartService.batchUpdateSelectedStatus(userId, cartItemIds, isSelected);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "全选/取消全选")
    @PutMapping("/select-all")
    public ApiResponse<Void> selectAll(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "是否全选") @RequestParam Boolean isSelected) {
        try {
            shoppingCartService.selectAll(userId, isSelected);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "获取购物车商品总数")
    @GetMapping("/count")
    public ApiResponse<Long> getCartItemCount(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            long count = shoppingCartService.getCartItemCount(userId);
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "获取购物车总金额")
    @GetMapping("/total")
    public ApiResponse<BigDecimal> getCartTotalAmount(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            BigDecimal total = shoppingCartService.getCartTotalAmount(userId);
            return ApiResponse.success(total);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "检查商品是否已在购物车中")
    @GetMapping("/check")
    public ApiResponse<Boolean> isProductInCart(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "商品ID") @RequestParam Long productId) {
        try {
            boolean exists = shoppingCartService.isProductInCart(userId, productId);
            return ApiResponse.success(exists);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "根据购物车项ID获取购物车项")
    @GetMapping("/item/{cartItemId}")
    public ApiResponse<ShoppingCart> getCartItemById(
            @Parameter(description = "购物车项ID") @PathVariable Long cartItemId) {
        try {
            ShoppingCart cart = shoppingCartService.getCartItemById(cartItemId);
            return ApiResponse.success(cart);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Operation(summary = "逻辑删除购物车项")
    @DeleteMapping("/soft-delete/{cartItemId}")
    public ApiResponse<Void> softDeleteCartItem(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "购物车项ID") @PathVariable Long cartItemId) {
        try {
            shoppingCartService.softDeleteCartItem(userId, cartItemId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
} 