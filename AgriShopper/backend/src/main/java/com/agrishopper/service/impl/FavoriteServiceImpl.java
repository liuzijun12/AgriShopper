package com.agrishopper.service.impl;

import com.agrishopper.model.Favorite;
import com.agrishopper.repository.FavoriteRepository;
import com.agrishopper.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 收藏服务实现类
 * 实现收藏相关的业务逻辑
 */
@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Override
    public Favorite addFavorite(Long userId, String productCode) {
        // 检查是否已经收藏
        if (isProductFavorited(userId, productCode)) {
            throw new RuntimeException("该商品已经收藏过了");
        }

        // 创建新的收藏记录
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductCode(productCode);

        return favoriteRepository.save(favorite);
    }

    @Override
    public void removeFavorite(Long userId, String productCode) {
        // 查找收藏记录
        Optional<Favorite> favoriteOpt = favoriteRepository.findByUserIdAndProductCode(userId, productCode);
        if (favoriteOpt.isPresent()) {
            favoriteRepository.delete(favoriteOpt.get());
        } else {
            throw new RuntimeException("收藏记录不存在");
        }
    }

    @Override
    public void removeFavorites(Long userId, List<String> productCodes) {
        if (productCodes != null && !productCodes.isEmpty()) {
            favoriteRepository.deleteByUserIdAndProductCodes(userId, productCodes);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Favorite> getUserFavorites(Long userId, Pageable pageable) {
        return favoriteRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getUserFavoritesOrderByCreateTime(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Favorite> getUserFavoritesOrderByCreateTime(Long userId, Pageable pageable) {
        return favoriteRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductFavorited(Long userId, String productCode) {
        return favoriteRepository.existsByUserIdAndProductCode(userId, productCode);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserFavoriteCount(Long userId) {
        return favoriteRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getProductFavoriteCount(String productCode) {
        return favoriteRepository.countByProductCode(productCode);
    }

    @Override
    @Transactional(readOnly = true)
    public Favorite getFavoriteById(Long favoriteId) {
        Optional<Favorite> favoriteOpt = favoriteRepository.findById(favoriteId);
        if (favoriteOpt.isPresent()) {
            return favoriteOpt.get();
        } else {
            throw new RuntimeException("收藏记录不存在");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Favorite getFavoriteByUserAndProduct(Long userId, String productCode) {
        Optional<Favorite> favoriteOpt = favoriteRepository.findByUserIdAndProductCode(userId, productCode);
        if (favoriteOpt.isPresent()) {
            return favoriteOpt.get();
        } else {
            throw new RuntimeException("收藏记录不存在");
        }
    }

    @Override
    public void clearUserFavorites(Long userId) {
        favoriteRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getProductFavorites(String productCode) {
        return favoriteRepository.findByProductCode(productCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getFavoritesByUserAndProducts(Long userId, List<String> productCodes) {
        if (productCodes != null && !productCodes.isEmpty()) {
            return favoriteRepository.findByUserIdAndProductCodes(userId, productCodes);
        }
        return List.of();
    }
} 