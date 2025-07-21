package com.agrishopper.service;

import com.agrishopper.model.UserBehavior;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户行为记录服务接口
 * 专门为推荐算法设计的业务逻辑层
 */
public interface UserBehaviorService {

    // ==================== 基础CRUD操作 ====================
    
    /**
     * 保存用户行为记录
     */
    UserBehavior saveUserBehavior(UserBehavior userBehavior);
    
    /**
     * 批量保存用户行为记录
     */
    List<UserBehavior> saveUserBehaviors(List<UserBehavior> userBehaviors);
    
    /**
     * 根据ID查询用户行为记录
     */
    UserBehavior findById(Long id);
    
    /**
     * 根据用户ID查询所有行为记录
     */
    List<UserBehavior> findByUserId(Long userId);
    
    /**
     * 根据用户ID和行为类型查询
     */
    List<UserBehavior> findByUserIdAndBehaviorType(Long userId, String behaviorType);
    
    /**
     * 删除用户行为记录
     */
    void deleteById(Long id);
    
    // ==================== 推荐算法相关业务方法 ====================
    
    /**
     * 记录用户查看商品行为
     */
    void recordViewProduct(Long userId, String productCode, String productName, String sourcePage);
    
    /**
     * 记录用户点击商品行为
     */
    void recordClickProduct(Long userId, String productCode, String productName, String sourcePage);
    
    /**
     * 记录用户加入购物车行为
     */
    void recordAddToCart(Long userId, String productCode, String productName, String sourcePage);
    
    /**
     * 记录用户收藏商品行为
     */
    void recordAddToFavorite(Long userId, String productCode, String productName, String sourcePage);
    
    /**
     * 记录用户取消收藏行为
     */
    void recordRemoveFromFavorite(Long userId, String productCode, String productName, String sourcePage);
    
    /**
     * 记录用户搜索行为
     */
    void recordSearchProduct(Long userId, String keyword, String sourcePage);
    
    /**
     * 记录用户查看分类行为
     */
    void recordViewCategory(Long userId, Long categoryId, String categoryName, String sourcePage);
    
    /**
     * 记录用户浏览页面行为
     */
    void recordViewPage(Long userId, String pagePath, String pageTitle, String sourcePage);
    
    // ==================== 个性化推荐方法 ====================
    
    /**
     * 获取用户个性化推荐商品列表
     * 基于用户历史行为偏好
     */
    List<String> getPersonalizedRecommendations(Long userId, int limit);
    
    /**
     * 获取用户最近访问的商品
     */
    List<String> getUserRecentProducts(Long userId, int limit);
    
    /**
     * 获取用户最常访问的商品
     */
    List<String> getUserMostVisitedProducts(Long userId, int limit);
    
    /**
     * 获取用户感兴趣的分类
     */
    List<Long> getUserPreferredCategories(Long userId, int limit);
    
    /**
     * 获取用户行为偏好分析
     */
    Map<String, Long> getUserBehaviorPreferences(Long userId);
    
    // ==================== 协同过滤推荐方法 ====================
    
    /**
     * 获取与用户相似的其他用户
     */
    List<Long> getSimilarUsers(Long userId, int limit);
    
    /**
     * 基于协同过滤的推荐商品
     */
    List<String> getCollaborativeFilteringRecommendations(Long userId, int limit);
    
    /**
     * 获取用户对商品的评分
     */
    Map<String, Integer> getUserProductRatings(Long userId);
    
    // ==================== 热门推荐方法 ====================
    
    /**
     * 获取全局热门商品
     */
    List<String> getGlobalHotProducts(int limit, int days);

    /**
     * 根据销量和库存获取默认推荐商品
     * 销量优先，库存次之
     */
    List<String> getDefaultRecommendationsBySalesAndStock(int limit);
    
    /**
     * 获取最近热门商品
     */
    List<String> getRecentHotProducts(int limit, int days);
    
    /**
     * 获取特定行为类型的热门商品
     */
    List<String> getHotProductsByBehaviorType(String behaviorType, int limit, int days);
    
    /**
     * 获取分类下的热门商品
     */
    List<String> getHotProductsByCategory(Long categoryId, int limit, int days);
    
    // ==================== 实时推荐方法 ====================
    
    /**
     * 获取实时推荐商品（基于用户最近行为）
     */
    List<String> getRealTimeRecommendations(Long userId, int limit);
    
    /**
     * 获取用户最近的行为记录
     */
    List<UserBehavior> getUserRecentBehaviors(Long userId, int limit);
    
    // ==================== 时间序列分析方法 ====================
    
    /**
     * 获取用户行为时间分布
     */
    Map<String, Long> getUserBehaviorTimeDistribution(Long userId, int days);
    
    /**
     * 获取用户活跃时间段
     */
    Map<Integer, Long> getUserActiveHours(Long userId, int days);
    
    /**
     * 获取用户活跃度分析
     */
    Map<String, Object> getUserActivityAnalysis(Long userId);
    
    // ==================== 统计分析方法 ====================
    
    /**
     * 获取用户行为统计信息
     */
    Map<String, Object> getUserBehaviorStatistics(Long userId);
    
    /**
     * 获取用户活跃度评分
     */
    Double getUserActivityScore(Long userId);
    
    /**
     * 判断用户是否为活跃用户
     */
    boolean isActiveUser(Long userId);
    
    /**
     * 获取用户行为趋势分析
     */
    Map<String, Object> getUserBehaviorTrend(Long userId, int days);
    
    /**
     * 清理过期数据
     */
    void cleanExpiredData(int days);
    
    // ==================== 推荐算法评估方法 ====================
    
    /**
     * 计算推荐准确率
     */
    Double calculateRecommendationAccuracy(Long userId, List<String> recommendedProducts, int days);
    
    /**
     * 计算推荐覆盖率
     */
    Double calculateRecommendationCoverage(List<String> recommendedProducts);
    
    /**
     * 计算推荐多样性
     */
    Double calculateRecommendationDiversity(List<String> recommendedProducts);
    
    // ==================== 数据清理和维护方法 ====================
    
    /**
     * 数据统计和汇总
     */
    void generateBehaviorSummary();
    
    /**
     * 导出用户行为数据
     */
    List<UserBehavior> exportUserBehaviorData(Long userId, LocalDateTime startTime, LocalDateTime endTime);
} 