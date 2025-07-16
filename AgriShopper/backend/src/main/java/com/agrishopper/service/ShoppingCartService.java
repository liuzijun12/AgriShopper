package com.agrishopper.service;

import com.agrishopper.model.ShoppingCart;
import java.math.BigDecimal;
import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加商品到购物车
     */
    ShoppingCart addToCart(Long userId, Long productId, Integer quantity);

    /**
     * 更新购物车商品数量
     */
    ShoppingCart updateQuantity(Long userId, Long productId, Integer quantity);

    /**
     * 从购物车移除商品
     */
    void removeFromCart(Long userId, Long productId);

    /**
     * 获取用户购物车列表
     */
    List<ShoppingCart> getUserCart(Long userId);

    /**
     * 获取用户选中的购物车项
     */
    List<ShoppingCart> getSelectedItems(Long userId);

    /**
     * 清空用户购物车
     */
    void clearCart(Long userId);

    /**
     * 清空用户选中的购物车项
     */
    void clearSelectedItems(Long userId);

    /**
     * 更新购物车项选中状态
     */
    void updateSelectedStatus(Long userId, Long cartItemId, Boolean isSelected);

    /**
     * 批量更新购物车项选中状态
     */
    void batchUpdateSelectedStatus(Long userId, List<Long> cartItemIds, Boolean isSelected);

    /**
     * 全选/取消全选
     */
    void selectAll(Long userId, Boolean isSelected);

    /**
     * 获取购物车商品总数
     */
    long getCartItemCount(Long userId);

    /**
     * 获取购物车总金额
     */
    BigDecimal getCartTotalAmount(Long userId);

    /**
     * 检查商品是否已在购物车中
     */
    boolean isProductInCart(Long userId, Long productId);

    /**
     * 根据购物车项ID获取购物车项
     */
    ShoppingCart getCartItemById(Long cartItemId);

    /**
     * 根据用户ID和商品ID获取购物车项
     */
    ShoppingCart getCartItemByUserAndProduct(Long userId, Long productId);

    /**
     * 逻辑删除购物车项
     */
    void softDeleteCartItem(Long userId, Long cartItemId);
} 