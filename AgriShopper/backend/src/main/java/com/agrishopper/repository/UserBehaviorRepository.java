package com.agrishopper.repository;

import com.agrishopper.model.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户行为记录仓库接口
 * 专门为推荐算法设计的数据访问层
 */
@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {

    // ==================== 基础查询方法 ====================
    
    /**
     * 根据用户ID查询用户的所有行为记录
     */
    List<UserBehavior> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 根据用户ID和行为类型查询
     */
    List<UserBehavior> findByUserIdAndBehaviorTypeOrderByCreateTimeDesc(Long userId, String behaviorType);
    
    /**
     * 根据目标类型和目标ID查询
     */
    List<UserBehavior> findByTargetTypeAndTargetIdOrderByCreateTimeDesc(String targetType, String targetId);
    
    /**
     * 根据时间范围查询
     */
    List<UserBehavior> findByCreateTimeBetweenOrderByCreateTimeDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    // ==================== 推荐算法专用查询方法 ====================
    
    /**
     * 获取用户最近的行为记录（用于实时推荐）
     */
    @Query("SELECT ub FROM UserBehavior ub WHERE ub.userId = :userId ORDER BY ub.createTime DESC")
    List<UserBehavior> findRecentBehaviorsByUserId(@Param("userId") Long userId);
    
    /**
     * 获取用户最近N条行为记录
     */
    @Query("SELECT ub FROM UserBehavior ub WHERE ub.userId = :userId ORDER BY ub.createTime DESC LIMIT :limit")
    List<UserBehavior> findRecentBehaviorsByUserIdWithLimit(@Param("userId") Long userId, @Param("limit") int limit);
    
    /**
     * 获取用户对特定商品的所有行为记录
     */
    @Query("SELECT ub FROM UserBehavior ub WHERE ub.userId = :userId AND ub.targetType = 'PRODUCT' AND ub.targetId = :productCode ORDER BY ub.createTime DESC")
    List<UserBehavior> findProductBehaviorsByUser(@Param("userId") Long userId, @Param("productCode") String productCode);
    
    /**
     * 统计用户对特定商品的行为次数
     */
    @Query("SELECT COUNT(ub) FROM UserBehavior ub WHERE ub.userId = :userId AND ub.targetType = 'PRODUCT' AND ub.targetId = :productCode")
    Long countUserProductBehaviors(@Param("userId") Long userId, @Param("productCode") String productCode);
    
    /**
     * 获取用户最常访问的商品（基于行为次数）
     */
    @Query("SELECT ub.targetId as productCode, COUNT(ub) as behaviorCount FROM UserBehavior ub " +
           "WHERE ub.userId = :userId AND ub.targetType = 'PRODUCT' " +
           "GROUP BY ub.targetId ORDER BY behaviorCount DESC")
    List<Object[]> findUserMostVisitedProducts(@Param("userId") Long userId);
    
    /**
     * 获取用户最近访问的商品（基于时间）
     */
    @Query("SELECT DISTINCT ub.targetId FROM UserBehavior ub " +
           "WHERE ub.userId = :userId AND ub.targetType = 'PRODUCT' " +
           "ORDER BY ub.createTime DESC")
    List<String> findUserRecentProducts(@Param("userId") Long userId);
    
    /**
     * 获取用户的行为偏好（按行为类型统计）
     */
    @Query("SELECT ub.behaviorType, COUNT(ub) as count FROM UserBehavior ub " +
           "WHERE ub.userId = :userId GROUP BY ub.behaviorType ORDER BY count DESC")
    List<Object[]> findUserBehaviorPreferences(@Param("userId") Long userId);
    
    // ==================== 协同过滤推荐相关查询 ====================
    
    /**
     * 找到与指定用户有相似行为的其他用户
     * 基于共同访问的商品
     */
    @Query("SELECT DISTINCT ub2.userId FROM UserBehavior ub1, UserBehavior ub2 " +
           "WHERE ub1.userId = :userId AND ub2.userId != :userId " +
           "AND ub1.targetType = 'PRODUCT' AND ub2.targetType = 'PRODUCT' " +
           "AND ub1.targetId = ub2.targetId")
    List<Long> findSimilarUsers(@Param("userId") Long userId);
    
    /**
     * 获取用户对商品的评分（基于行为类型加权）
     */
    @Query("SELECT ub.targetId as productCode, " +
           "CASE ub.behaviorType " +
           "WHEN 'ADD_TO_CART' THEN 5 " +
           "WHEN 'ADD_TO_FAVORITE' THEN 4 " +
           "WHEN 'CLICK_PRODUCT' THEN 3 " +
           "WHEN 'VIEW_PRODUCT' THEN 2 " +
           "ELSE 1 END as rating " +
           "FROM UserBehavior ub " +
           "WHERE ub.userId = :userId AND ub.targetType = 'PRODUCT'")
    List<Object[]> findUserProductRatings(@Param("userId") Long userId);
    
    // ==================== 热门商品推荐相关查询 ====================
    
    /**
     * 获取全局热门商品（基于所有用户的行为）
     */
    @Query("SELECT ub.targetId as productCode, COUNT(ub) as behaviorCount FROM UserBehavior ub " +
           "WHERE ub.targetType = 'PRODUCT' AND ub.createTime >= :startTime " +
           "GROUP BY ub.targetId ORDER BY behaviorCount DESC")
    List<Object[]> findGlobalHotProducts(@Param("startTime") LocalDateTime startTime);
    
    /**
     * 获取最近热门商品（基于时间窗口）
     */
    @Query("SELECT ub.targetId as productCode, COUNT(ub) as behaviorCount FROM UserBehavior ub " +
           "WHERE ub.targetType = 'PRODUCT' AND ub.createTime BETWEEN :startTime AND :endTime " +
           "GROUP BY ub.targetId ORDER BY behaviorCount DESC")
    List<Object[]> findRecentHotProducts(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取特定行为类型的热门商品
     */
    @Query("SELECT ub.targetId as productCode, COUNT(ub) as behaviorCount FROM UserBehavior ub " +
           "WHERE ub.targetType = 'PRODUCT' AND ub.behaviorType = :behaviorType " +
           "AND ub.createTime >= :startTime " +
           "GROUP BY ub.targetId ORDER BY behaviorCount DESC")
    List<Object[]> findHotProductsByBehaviorType(@Param("behaviorType") String behaviorType, @Param("startTime") LocalDateTime startTime);
    
    // ==================== 分类推荐相关查询 ====================
    
    /**
     * 获取用户最感兴趣的分类（基于商品分类）
     */
    @Query("SELECT p.categoryId, COUNT(ub) as behaviorCount FROM UserBehavior ub " +
           "JOIN Product p ON ub.targetId = p.productCode " +
           "WHERE ub.userId = :userId AND ub.targetType = 'PRODUCT' " +
           "GROUP BY p.categoryId ORDER BY behaviorCount DESC")
    List<Object[]> findUserPreferredCategories(@Param("userId") Long userId);
    
    /**
     * 获取分类下的热门商品
     */
    @Query("SELECT ub.targetId as productCode, COUNT(ub) as behaviorCount FROM UserBehavior ub " +
           "JOIN Product p ON ub.targetId = p.productCode " +
           "WHERE ub.targetType = 'PRODUCT' AND p.categoryId = :categoryId " +
           "AND ub.createTime >= :startTime " +
           "GROUP BY ub.targetId ORDER BY behaviorCount DESC")
    List<Object[]> findHotProductsByCategory(@Param("categoryId") Long categoryId, @Param("startTime") LocalDateTime startTime);
    
    // ==================== 时间序列分析相关查询 ====================
    
    /**
     * 获取用户的行为时间分布
     */
    @Query("SELECT DATE(ub.createTime) as date, COUNT(ub) as behaviorCount FROM UserBehavior ub " +
           "WHERE ub.userId = :userId AND ub.createTime >= :startTime " +
           "GROUP BY DATE(ub.createTime) ORDER BY date DESC")
    List<Object[]> findUserBehaviorTimeDistribution(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);
    
    /**
     * 获取用户活跃时间段
     */
    @Query("SELECT HOUR(ub.createTime) as hour, COUNT(ub) as behaviorCount FROM UserBehavior ub " +
           "WHERE ub.userId = :userId AND ub.createTime >= :startTime " +
           "GROUP BY HOUR(ub.createTime) ORDER BY behaviorCount DESC")
    List<Object[]> findUserActiveHours(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);
    
    // ==================== 统计分析方法 ====================
    
    /**
     * 统计用户总行为数
     */
    @Query("SELECT COUNT(ub) FROM UserBehavior ub WHERE ub.userId = :userId")
    Long countUserTotalBehaviors(@Param("userId") Long userId);
    
    /**
     * 统计用户活跃天数
     */
    @Query("SELECT COUNT(DISTINCT DATE(ub.createTime)) FROM UserBehavior ub WHERE ub.userId = :userId")
    Long countUserActiveDays(@Param("userId") Long userId);
    
    /**
     * 获取用户首次行为时间
     */
    @Query("SELECT MIN(ub.createTime) FROM UserBehavior ub WHERE ub.userId = :userId")
    LocalDateTime findUserFirstBehaviorTime(@Param("userId") Long userId);
    
    /**
     * 获取用户最后行为时间
     */
    @Query("SELECT MAX(ub.createTime) FROM UserBehavior ub WHERE ub.userId = :userId")
    LocalDateTime findUserLastBehaviorTime(@Param("userId") Long userId);
} 