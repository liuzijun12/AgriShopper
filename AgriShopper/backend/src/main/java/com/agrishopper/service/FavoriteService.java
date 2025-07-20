package com.agrishopper.service;

import com.agrishopper.model.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 收藏服务接口
 * 定义收藏相关的业务操作方法
 */
public interface FavoriteService {

    /**
     * 添加收藏
     * @param userId 用户ID
     * @param productCode 商品编号
     * @return 收藏对象
     */
    Favorite addFavorite(Long userId, String productCode);

    /**
     * 取消收藏
     * @param userId 用户ID
     * @param productCode 商品编号
     */
    void removeFavorite(Long userId, String productCode);

    /**
     * 批量取消收藏
     * @param userId 用户ID
     * @param productCodes 商品编号列表
     */
    void removeFavorites(Long userId, List<String> productCodes);

    /**
     * 获取用户收藏列表
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<Favorite> getUserFavorites(Long userId);

    /**
     * 分页获取用户收藏列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页收藏列表
     */
    Page<Favorite> getUserFavorites(Long userId, Pageable pageable);

    /**
     * 获取用户收藏列表（按创建时间倒序）
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<Favorite> getUserFavoritesOrderByCreateTime(Long userId);

    /**
     * 分页获取用户收藏列表（按创建时间倒序）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页收藏列表
     */
    Page<Favorite> getUserFavoritesOrderByCreateTime(Long userId, Pageable pageable);

    /**
     * 检查用户是否已收藏某商品
     * @param userId 用户ID
     * @param productCode 商品编号
     * @return 是否已收藏
     */
    boolean isProductFavorited(Long userId, String productCode);

    /**
     * 获取用户收藏数量
     * @param userId 用户ID
     * @return 收藏数量
     */
    long getUserFavoriteCount(Long userId);

    /**
     * 获取商品被收藏数量
     * @param productCode 商品编号
     * @return 收藏数量
     */
    long getProductFavoriteCount(String productCode);

    /**
     * 根据收藏ID获取收藏信息
     * @param favoriteId 收藏ID
     * @return 收藏对象
     */
    Favorite getFavoriteById(Long favoriteId);

    /**
     * 根据用户ID和商品编号获取收藏信息
     * @param userId 用户ID
     * @param productCode 商品编号
     * @return 收藏对象
     */
    Favorite getFavoriteByUserAndProduct(Long userId, String productCode);

    /**
     * 清空用户所有收藏
     * @param userId 用户ID
     */
    void clearUserFavorites(Long userId);

    /**
     * 获取收藏某商品的所有用户
     * @param productCode 商品编号
     * @return 收藏列表
     */
    List<Favorite> getProductFavorites(String productCode);

    /**
     * 根据用户ID和商品编号列表获取收藏信息
     * @param userId 用户ID
     * @param productCodes 商品编号列表
     * @return 收藏列表
     */
    List<Favorite> getFavoritesByUserAndProducts(Long userId, List<String> productCodes);
} 