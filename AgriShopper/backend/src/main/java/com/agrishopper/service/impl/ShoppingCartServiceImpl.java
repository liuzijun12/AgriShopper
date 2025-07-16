package com.agrishopper.service.impl;

import com.agrishopper.model.Product;
import com.agrishopper.model.ShoppingCart;
import com.agrishopper.repository.ProductRepository;
import com.agrishopper.repository.ShoppingCartRepository;
import com.agrishopper.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ShoppingCart addToCart(Long userId, Long productId, Integer quantity) {
        // 检查商品是否存在
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 检查库存
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        // 检查商品是否已在购物车中
        Optional<ShoppingCart> existingCart = shoppingCartRepository.findByUserIdAndProductIdAndIsDeletedFalse(userId, productId);
        
        if (existingCart.isPresent()) {
            // 如果已存在，更新数量
            ShoppingCart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            cart.setUnitPrice(product.getProductPrice());
            cart.calculateSubtotal();
            return shoppingCartRepository.save(cart);
        } else {
            // 如果不存在，创建新的购物车项
            ShoppingCart cart = new ShoppingCart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setUnitPrice(product.getProductPrice());
            cart.setProduct(product);
            cart.calculateSubtotal();
            return shoppingCartRepository.save(cart);
        }
    }

    @Override
    public ShoppingCart updateQuantity(Long userId, Long productId, Integer quantity) {
        // 检查商品是否存在
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 检查库存
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        // 查找购物车项
        ShoppingCart cart = shoppingCartRepository.findByUserIdAndProductIdAndIsDeletedFalse(userId, productId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));

        cart.setQuantity(quantity);
        cart.setUnitPrice(product.getProductPrice());
        cart.calculateSubtotal();
        return shoppingCartRepository.save(cart);
    }

    @Override
    public void removeFromCart(Long userId, Long productId) {
        shoppingCartRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public List<ShoppingCart> getUserCart(Long userId) {
        List<ShoppingCart> cartItems = shoppingCartRepository.findByUserIdWithProduct(userId);
        
        // 填充商品信息
        for (ShoppingCart cart : cartItems) {
            if (cart.getProduct() == null) {
                Product product = productRepository.findById(cart.getProductId()).orElse(null);
                cart.setProduct(product);
            }
        }
        
        return cartItems;
    }

    @Override
    public List<ShoppingCart> getSelectedItems(Long userId) {
        List<ShoppingCart> selectedItems = shoppingCartRepository.findByUserIdAndIsSelectedTrueAndIsDeletedFalse(userId);
        
        // 填充商品信息
        for (ShoppingCart cart : selectedItems) {
            if (cart.getProduct() == null) {
                Product product = productRepository.findById(cart.getProductId()).orElse(null);
                cart.setProduct(product);
            }
        }
        
        return selectedItems;
    }

    @Override
    public void clearCart(Long userId) {
        shoppingCartRepository.deleteByUserId(userId);
    }

    @Override
    public void clearSelectedItems(Long userId) {
        shoppingCartRepository.deleteByUserIdAndIsSelectedTrue(userId);
    }

    @Override
    public void updateSelectedStatus(Long userId, Long cartItemId, Boolean isSelected) {
        ShoppingCart cart = shoppingCartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));
        
        if (!cart.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此购物车项");
        }
        
        cart.setIsSelected(isSelected);
        shoppingCartRepository.save(cart);
    }

    @Override
    public void batchUpdateSelectedStatus(Long userId, List<Long> cartItemIds, Boolean isSelected) {
        shoppingCartRepository.updateSelectedStatus(userId, cartItemIds, isSelected);
    }

    @Override
    public void selectAll(Long userId, Boolean isSelected) {
        List<ShoppingCart> cartItems = shoppingCartRepository.findByUserIdAndIsDeletedFalse(userId);
        for (ShoppingCart cart : cartItems) {
            cart.setIsSelected(isSelected);
        }
        shoppingCartRepository.saveAll(cartItems);
    }

    @Override
    public long getCartItemCount(Long userId) {
        return shoppingCartRepository.countByUserIdAndIsDeletedFalse(userId);
    }

    @Override
    public BigDecimal getCartTotalAmount(Long userId) {
        return shoppingCartRepository.calculateTotalAmount(userId);
    }

    @Override
    public boolean isProductInCart(Long userId, Long productId) {
        return shoppingCartRepository.existsByUserIdAndProductIdAndIsDeletedFalse(userId, productId);
    }

    @Override
    public ShoppingCart getCartItemById(Long cartItemId) {
        return shoppingCartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));
    }

    @Override
    public ShoppingCart getCartItemByUserAndProduct(Long userId, Long productId) {
        return shoppingCartRepository.findByUserIdAndProductIdAndIsDeletedFalse(userId, productId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));
    }

    @Override
    public void softDeleteCartItem(Long userId, Long cartItemId) {
        int updatedRows = shoppingCartRepository.softDeleteCartItem(cartItemId, userId);
        if (updatedRows == 0) {
            throw new RuntimeException("购物车项不存在或无权限删除");
        }
    }
} 