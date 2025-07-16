package com.agrishopper.repository;

import com.agrishopper.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    /**
     * 根据用户ID查找购物车项（未删除的）
     */
    List<ShoppingCart> findByUserIdAndIsDeletedFalse(Long userId);

    /**
     * 根据用户ID查找选中的购物车项（未删除的）
     */
    List<ShoppingCart> findByUserIdAndIsSelectedTrueAndIsDeletedFalse(Long userId);

    /**
     * 根据用户ID和商品ID查找购物车项（未删除的）
     */
    Optional<ShoppingCart> findByUserIdAndProductIdAndIsDeletedFalse(Long userId, Long productId);

    /**
     * 检查用户是否已将商品加入购物车（未删除的）
     */
    boolean existsByUserIdAndProductIdAndIsDeletedFalse(Long userId, Long productId);

    /**
     * 根据用户ID统计购物车项数量（未删除的）
     */
    long countByUserIdAndIsDeletedFalse(Long userId);

    /**
     * 根据用户ID统计选中的购物车项数量（未删除的）
     */
    long countByUserIdAndIsSelectedTrueAndIsDeletedFalse(Long userId);

    /**
     * 根据用户ID删除所有购物车项
     */
    void deleteByUserId(Long userId);

    /**
     * 根据用户ID删除选中的购物车项
     */
    void deleteByUserIdAndIsSelectedTrue(Long userId);

    /**
     * 根据用户ID和商品ID删除购物车项
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);

    /**
     * 批量更新购物车项的选中状态
     */
    @Modifying
    @Query("UPDATE ShoppingCart sc SET sc.isSelected = :isSelected WHERE sc.userId = :userId AND sc.id IN :cartItemIds")
    int updateSelectedStatus(@Param("userId") Long userId, @Param("cartItemIds") List<Long> cartItemIds, @Param("isSelected") Boolean isSelected);

    /**
     * 根据用户ID计算购物车总金额（未删除的）
     */
    @Query("SELECT COALESCE(SUM(sc.subtotal), 0) FROM ShoppingCart sc WHERE sc.userId = :userId AND sc.isSelected = true AND sc.isDeleted = false")
    java.math.BigDecimal calculateTotalAmount(@Param("userId") Long userId);

    /**
     * 根据用户ID查找购物车项（包含商品信息，未删除的）
     */
    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.userId = :userId AND sc.isDeleted = false ORDER BY sc.createTime DESC")
    List<ShoppingCart> findByUserIdWithProduct(@Param("userId") Long userId);

    /**
     * 逻辑删除购物车项
     */
    @Modifying
    @Query("UPDATE ShoppingCart sc SET sc.isDeleted = true WHERE sc.id = :cartItemId AND sc.userId = :userId")
    int softDeleteCartItem(@Param("cartItemId") Long cartItemId, @Param("userId") Long userId);
} 