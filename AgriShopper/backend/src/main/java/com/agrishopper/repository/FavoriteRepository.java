package com.agrishopper.repository;

import com.agrishopper.model.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 收藏仓库接口
 * 提供收藏相关的数据库操作方法
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * 根据用户ID查找所有收藏
     */
    List<Favorite> findByUserId(Long userId);

    /**
     * 根据用户ID分页查找收藏
     */
    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和商品编号查找收藏
     */
    Optional<Favorite> findByUserIdAndProductCode(Long userId, String productCode);

    /**
     * 检查用户是否已收藏某商品
     */
    boolean existsByUserIdAndProductCode(Long userId, String productCode);

    /**
     * 根据用户ID统计收藏数量
     */
    long countByUserId(Long userId);

    /**
     * 根据用户ID删除所有收藏
     */
    void deleteByUserId(Long userId);

    /**
     * 根据用户ID和商品编号删除收藏
     */
    void deleteByUserIdAndProductCode(Long userId, String productCode);

    /**
     * 根据商品编号查找所有收藏该商品的用户
     */
    List<Favorite> findByProductCode(String productCode);

    /**
     * 根据商品编号统计收藏该商品的用户数量
     */
    long countByProductCode(String productCode);

    /**
     * 根据用户ID查找收藏（按创建时间倒序）
     */
    @Query("SELECT f FROM Favorite f WHERE f.userId = :userId ORDER BY f.createTime DESC")
    List<Favorite> findByUserIdOrderByCreateTimeDesc(@Param("userId") Long userId);

    /**
     * 根据用户ID分页查找收藏（按创建时间倒序）
     */
    @Query("SELECT f FROM Favorite f WHERE f.userId = :userId ORDER BY f.createTime DESC")
    Page<Favorite> findByUserIdOrderByCreateTimeDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * 批量删除收藏
     */
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.userId = :userId AND f.productCode IN :productCodes")
    int deleteByUserIdAndProductCodes(@Param("userId") Long userId, @Param("productCodes") List<String> productCodes);

    /**
     * 根据用户ID和商品编号列表查找收藏
     */
    @Query("SELECT f FROM Favorite f WHERE f.userId = :userId AND f.productCode IN :productCodes")
    List<Favorite> findByUserIdAndProductCodes(@Param("userId") Long userId, @Param("productCodes") List<String> productCodes);
} 