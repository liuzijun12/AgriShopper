package com.agrishopper.service.impl;

import com.agrishopper.model.UserBehavior;
import com.agrishopper.repository.UserBehaviorRepository;
import com.agrishopper.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户行为记录服务实现类
 * 专门为推荐算法设计的业务逻辑实现
 */
@Service
@Transactional
public class UserBehaviorServiceImpl implements UserBehaviorService {

    @Autowired
    private UserBehaviorRepository userBehaviorRepository;

    // ==================== 基础CRUD操作 ====================
    
    @Override
    public UserBehavior saveUserBehavior(UserBehavior userBehavior) {
        return userBehaviorRepository.save(userBehavior);
    }
    
    @Override
    public List<UserBehavior> saveUserBehaviors(List<UserBehavior> userBehaviors) {
        return userBehaviorRepository.saveAll(userBehaviors);
    }
    
    @Override
    public UserBehavior findById(Long id) {
        return userBehaviorRepository.findById(id).orElse(null);
    }
    
    @Override
    public List<UserBehavior> findByUserId(Long userId) {
        return userBehaviorRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
    
    @Override
    public List<UserBehavior> findByUserIdAndBehaviorType(Long userId, String behaviorType) {
        return userBehaviorRepository.findByUserIdAndBehaviorTypeOrderByCreateTimeDesc(userId, behaviorType);
    }
    
    @Override
    public void deleteById(Long id) {
        userBehaviorRepository.deleteById(id);
    }
    
    // ==================== 推荐算法相关业务方法 ====================
    
    @Override
    public void recordViewProduct(Long userId, String productCode, String productName, String sourcePage) {
        UserBehavior behavior = createUserBehavior(userId, "VIEW_PRODUCT", "PRODUCT", productCode, productName, sourcePage);
        userBehaviorRepository.save(behavior);
    }
    
    @Override
    public void recordClickProduct(Long userId, String productCode, String productName, String sourcePage) {
        UserBehavior behavior = createUserBehavior(userId, "CLICK_PRODUCT", "PRODUCT", productCode, productName, sourcePage);
        userBehaviorRepository.save(behavior);
    }
    
    @Override
    public void recordAddToCart(Long userId, String productCode, String productName, String sourcePage) {
        UserBehavior behavior = createUserBehavior(userId, "ADD_TO_CART", "PRODUCT", productCode, productName, sourcePage);
        userBehaviorRepository.save(behavior);
    }
    
    @Override
    public void recordAddToFavorite(Long userId, String productCode, String productName, String sourcePage) {
        UserBehavior behavior = createUserBehavior(userId, "ADD_TO_FAVORITE", "PRODUCT", productCode, productName, sourcePage);
        userBehaviorRepository.save(behavior);
    }
    
    @Override
    public void recordRemoveFromFavorite(Long userId, String productCode, String productName, String sourcePage) {
        UserBehavior behavior = createUserBehavior(userId, "REMOVE_FROM_FAVORITE", "PRODUCT", productCode, productName, sourcePage);
        userBehaviorRepository.save(behavior);
    }
    
    @Override
    public void recordSearchProduct(Long userId, String keyword, String sourcePage) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setBehaviorType("SEARCH_PRODUCT");
        behavior.setTargetType("SEARCH");
        behavior.setTargetId(keyword);
        behavior.setTargetName("搜索关键词：" + keyword);
        behavior.setSourcePage(sourcePage);
        behavior.setCreateTime(LocalDateTime.now());
        behavior.setUpdateTime(LocalDateTime.now());
        userBehaviorRepository.save(behavior);
    }
    
    @Override
    public void recordViewCategory(Long userId, Long categoryId, String categoryName, String sourcePage) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setBehaviorType("VIEW_CATEGORY");
        behavior.setTargetType("CATEGORY");
        behavior.setTargetId(categoryId.toString());
        behavior.setTargetName(categoryName);
        behavior.setSourcePage(sourcePage);
        behavior.setCreateTime(LocalDateTime.now());
        behavior.setUpdateTime(LocalDateTime.now());
        userBehaviorRepository.save(behavior);
    }
    
    @Override
    public void recordViewPage(Long userId, String pagePath, String pageTitle, String sourcePage) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setBehaviorType("VIEW_PAGE");
        behavior.setTargetType("PAGE");
        behavior.setTargetId(pagePath);
        behavior.setTargetName(pageTitle);
        behavior.setSourcePage(sourcePage);
        behavior.setCreateTime(LocalDateTime.now());
        behavior.setUpdateTime(LocalDateTime.now());
        userBehaviorRepository.save(behavior);
    }
    
    // ==================== 个性化推荐方法 ====================
    
    @Override
    public List<String> getPersonalizedRecommendations(Long userId, int limit) {
        // 获取用户最近访问的商品和最常访问的商品，进行混合推荐
        List<String> recentProducts = getUserRecentProducts(userId, limit / 2);
        List<String> mostVisitedProducts = getUserMostVisitedProducts(userId, limit / 2);
        
        Set<String> recommendations = new LinkedHashSet<>();
        recommendations.addAll(recentProducts);
        recommendations.addAll(mostVisitedProducts);
        
        return recommendations.stream().limit(limit).collect(Collectors.toList());
    }
    
    @Override
    public List<String> getUserRecentProducts(Long userId, int limit) {
        return userBehaviorRepository.findUserRecentProducts(userId).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getUserMostVisitedProducts(Long userId, int limit) {
        List<Object[]> results = userBehaviorRepository.findUserMostVisitedProducts(userId);
        return results.stream()
                .limit(limit)
                .map(result -> (String) result[0])
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Long> getUserPreferredCategories(Long userId, int limit) {
        List<Object[]> results = userBehaviorRepository.findUserPreferredCategories(userId);
        return results.stream()
                .limit(limit)
                .map(result -> (Long) result[0])
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Long> getUserBehaviorPreferences(Long userId) {
        List<Object[]> results = userBehaviorRepository.findUserBehaviorPreferences(userId);
        Map<String, Long> preferences = new HashMap<>();
        for (Object[] result : results) {
            preferences.put((String) result[0], (Long) result[1]);
        }
        return preferences;
    }
    
    // ==================== 协同过滤推荐方法 ====================
    
    @Override
    public List<Long> getSimilarUsers(Long userId, int limit) {
        return userBehaviorRepository.findSimilarUsers(userId).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getCollaborativeFilteringRecommendations(Long userId, int limit) {
        // 获取相似用户
        List<Long> similarUsers = getSimilarUsers(userId, limit * 2);
        
        // 获取相似用户访问的商品
        Set<String> recommendations = new HashSet<>();
        for (Long similarUserId : similarUsers) {
            List<String> userProducts = getUserRecentProducts(similarUserId, limit);
            recommendations.addAll(userProducts);
        }
        
        return recommendations.stream().limit(limit).collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Integer> getUserProductRatings(Long userId) {
        List<Object[]> results = userBehaviorRepository.findUserProductRatings(userId);
        Map<String, Integer> ratings = new HashMap<>();
        for (Object[] result : results) {
            ratings.put((String) result[0], (Integer) result[1]);
        }
        return ratings;
    }
    
    // ==================== 热门推荐方法 ====================
    
    @Override
    public List<String> getGlobalHotProducts(int limit, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        List<Object[]> results = userBehaviorRepository.findGlobalHotProducts(startTime);
        return results.stream()
                .limit(limit)
                .map(result -> (String) result[0])
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getRecentHotProducts(int limit, int days) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(days);
        List<Object[]> results = userBehaviorRepository.findRecentHotProducts(startTime, endTime);
        return results.stream()
                .limit(limit)
                .map(result -> (String) result[0])
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getHotProductsByBehaviorType(String behaviorType, int limit, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        List<Object[]> results = userBehaviorRepository.findHotProductsByBehaviorType(behaviorType, startTime);
        return results.stream()
                .limit(limit)
                .map(result -> (String) result[0])
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getHotProductsByCategory(Long categoryId, int limit, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        List<Object[]> results = userBehaviorRepository.findHotProductsByCategory(categoryId, startTime);
        return results.stream()
                .limit(limit)
                .map(result -> (String) result[0])
                .collect(Collectors.toList());
    }
    
    // ==================== 实时推荐方法 ====================
    
    @Override
    public List<String> getRealTimeRecommendations(Long userId, int limit) {
        // 基于用户最近行为进行实时推荐
        List<UserBehavior> recentBehaviors = getUserRecentBehaviors(userId, limit * 2);
        
        // 提取最近访问的商品
        Set<String> recentProducts = recentBehaviors.stream()
                .filter(behavior -> "PRODUCT".equals(behavior.getTargetType()))
                .map(UserBehavior::getTargetId)
                .collect(Collectors.toSet());
        
        return recentProducts.stream().limit(limit).collect(Collectors.toList());
    }
    
    @Override
    public List<UserBehavior> getUserRecentBehaviors(Long userId, int limit) {
        return userBehaviorRepository.findRecentBehaviorsByUserIdWithLimit(userId, limit);
    }
    
    // ==================== 时间序列分析方法 ====================
    
    @Override
    public Map<String, Long> getUserBehaviorTimeDistribution(Long userId, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        List<Object[]> results = userBehaviorRepository.findUserBehaviorTimeDistribution(userId, startTime);
        Map<String, Long> distribution = new HashMap<>();
        for (Object[] result : results) {
            distribution.put(result[0].toString(), (Long) result[1]);
        }
        return distribution;
    }
    
    @Override
    public Map<Integer, Long> getUserActiveHours(Long userId, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        List<Object[]> results = userBehaviorRepository.findUserActiveHours(userId, startTime);
        Map<Integer, Long> activeHours = new HashMap<>();
        for (Object[] result : results) {
            activeHours.put((Integer) result[0], (Long) result[1]);
        }
        return activeHours;
    }
    
    @Override
    public Map<String, Object> getUserActivityAnalysis(Long userId) {
        Map<String, Object> analysis = new HashMap<>();
        
        // 获取用户活跃时间段
        Map<Integer, Long> activeHours = getUserActiveHours(userId, 30);
        analysis.put("activeHours", activeHours);
        
        // 获取用户行为时间分布
        Map<String, Long> timeDistribution = getUserBehaviorTimeDistribution(userId, 30);
        analysis.put("timeDistribution", timeDistribution);
        
        // 获取用户活跃度评分
        Double activityScore = getUserActivityScore(userId);
        analysis.put("activityScore", activityScore);
        
        return analysis;
    }
    
    // ==================== 统计分析方法 ====================
    
    @Override
    public Map<String, Object> getUserBehaviorStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总行为数
        Long totalBehaviors = userBehaviorRepository.countUserTotalBehaviors(userId);
        statistics.put("totalBehaviors", totalBehaviors);
        
        // 活跃天数
        Long activeDays = userBehaviorRepository.countUserActiveDays(userId);
        statistics.put("activeDays", activeDays);
        
        // 首次行为时间
        LocalDateTime firstBehaviorTime = userBehaviorRepository.findUserFirstBehaviorTime(userId);
        statistics.put("firstBehaviorTime", firstBehaviorTime);
        
        // 最后行为时间
        LocalDateTime lastBehaviorTime = userBehaviorRepository.findUserLastBehaviorTime(userId);
        statistics.put("lastBehaviorTime", lastBehaviorTime);
        
        // 行为偏好
        Map<String, Long> preferences = getUserBehaviorPreferences(userId);
        statistics.put("behaviorPreferences", preferences);
        
        return statistics;
    }
    
    @Override
    public Double getUserActivityScore(Long userId) {
        // 计算用户活跃度评分（基于行为频率、多样性等）
        Long totalBehaviors = userBehaviorRepository.countUserTotalBehaviors(userId);
        Long activeDays = userBehaviorRepository.countUserActiveDays(userId);
        
        if (activeDays == 0) return 0.0;
        
        // 简单的活跃度计算公式：平均每天行为数
        return (double) totalBehaviors / activeDays;
    }
    
    @Override
    public boolean isActiveUser(Long userId) {
        // 判断用户是否为活跃用户（最近7天有行为）
        LocalDateTime lastBehaviorTime = userBehaviorRepository.findUserLastBehaviorTime(userId);
        if (lastBehaviorTime == null) return false;
        
        return lastBehaviorTime.isAfter(LocalDateTime.now().minusDays(7));
    }
    
    @Override
    public Map<String, Object> getUserBehaviorTrend(Long userId, int days) {
        Map<String, Object> trend = new HashMap<>();
        
        // 获取用户行为时间分布
        Map<String, Long> timeDistribution = getUserBehaviorTimeDistribution(userId, days);
        trend.put("timeDistribution", timeDistribution);
        
        // 计算趋势（简单示例：比较前半段和后半段的行为数）
        List<Long> behaviorCounts = new ArrayList<>(timeDistribution.values());
        if (behaviorCounts.size() >= 2) {
            int mid = behaviorCounts.size() / 2;
            long firstHalf = behaviorCounts.subList(0, mid).stream().mapToLong(Long::longValue).sum();
            long secondHalf = behaviorCounts.subList(mid, behaviorCounts.size()).stream().mapToLong(Long::longValue).sum();
            
            String trendDirection = secondHalf > firstHalf ? "上升" : secondHalf < firstHalf ? "下降" : "稳定";
            trend.put("trendDirection", trendDirection);
            trend.put("trendRatio", secondHalf > 0 ? (double) firstHalf / secondHalf : 0.0);
        }
        
        return trend;
    }
    
    // ==================== 推荐算法评估方法 ====================
    
    @Override
    public Double calculateRecommendationAccuracy(Long userId, List<String> recommendedProducts, int days) {
        // 计算推荐准确率（推荐的商品中用户实际访问的比例）
        if (recommendedProducts.isEmpty()) return 0.0;
        
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        List<UserBehavior> userBehaviors = userBehaviorRepository.findByUserIdAndBehaviorTypeOrderByCreateTimeDesc(userId, "VIEW_PRODUCT");
        
        Set<String> userViewedProducts = userBehaviors.stream()
                .filter(behavior -> behavior.getCreateTime().isAfter(startTime))
                .map(UserBehavior::getTargetId)
                .collect(Collectors.toSet());
        
        long hitCount = recommendedProducts.stream()
                .filter(userViewedProducts::contains)
                .count();
        
        return (double) hitCount / recommendedProducts.size();
    }
    
    @Override
    public Double calculateRecommendationCoverage(List<String> recommendedProducts) {
        // 计算推荐覆盖率（推荐商品占总体商品的比例）
        // 这里简化处理，实际需要查询总商品数
        if (recommendedProducts.isEmpty()) return 0.0;
        
        // 假设总商品数为1000（实际应该从数据库查询）
        int totalProducts = 1000;
        return (double) recommendedProducts.size() / totalProducts;
    }
    
    @Override
    public Double calculateRecommendationDiversity(List<String> recommendedProducts) {
        // 计算推荐多样性（推荐商品中不同分类的比例）
        if (recommendedProducts.isEmpty()) return 0.0;
        
        // 这里简化处理，实际需要查询商品分类信息
        // 假设每个商品都有不同的分类
        Set<String> uniqueCategories = new HashSet<>();
        for (String productCode : recommendedProducts) {
            // 实际应该查询商品分类
            uniqueCategories.add("category_" + productCode.hashCode() % 10);
        }
        
        return (double) uniqueCategories.size() / recommendedProducts.size();
    }
    
    // ==================== 数据清理和维护方法 ====================
    
    @Override
    public void cleanExpiredData(int days) {
        // 清理指定天数前的数据
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(days);
        // 这里需要实现具体的清理逻辑
        // 可以使用 @Query 注解的删除方法
    }
    
    @Override
    public void generateBehaviorSummary() {
        // 生成行为数据汇总
        // 可以生成各种统计报表
    }
    
    @Override
    public List<UserBehavior> exportUserBehaviorData(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return userBehaviorRepository.findByUserIdAndBehaviorTypeOrderByCreateTimeDesc(userId, "VIEW_PRODUCT")
                .stream()
                .filter(behavior -> behavior.getCreateTime().isAfter(startTime) && behavior.getCreateTime().isBefore(endTime))
                .collect(Collectors.toList());
    }
    
    // ==================== 私有辅助方法 ====================
    
    private UserBehavior createUserBehavior(Long userId, String behaviorType, String targetType, 
                                          String targetId, String targetName, String sourcePage) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setBehaviorType(behaviorType);
        behavior.setTargetType(targetType);
        behavior.setTargetId(targetId);
        behavior.setTargetName(targetName);
        behavior.setSourcePage(sourcePage);
        behavior.setCreateTime(LocalDateTime.now());
        behavior.setUpdateTime(LocalDateTime.now());
        return behavior;
    }
} 