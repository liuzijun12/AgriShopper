package com.agrishopper.repository;

import com.agrishopper.model.CustomerServiceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerServiceSessionRepository extends JpaRepository<CustomerServiceSession, Long> {
    
    /**
     * 根据会话编码查找会话
     */
    Optional<CustomerServiceSession> findBySessionCode(String sessionCode);
    
    /**
     * 根据用户ID查找所有会话（未删除）
     */
    @Query("SELECT s FROM CustomerServiceSession s WHERE s.userId = :userId AND (s.isDeleted = false OR s.isDeleted IS NULL) ORDER BY s.createTime DESC")
    List<CustomerServiceSession> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和会话状态查找会话（未删除）
     */
    @Query("SELECT s FROM CustomerServiceSession s WHERE s.userId = :userId AND s.sessionStatus = :sessionStatus AND (s.isDeleted = false OR s.isDeleted IS NULL) ORDER BY s.createTime DESC")
    List<CustomerServiceSession> findByUserIdAndStatus(@Param("userId") Long userId, @Param("sessionStatus") Integer sessionStatus);
    
    /**
     * 根据用户ID、产品ID和会话状态查找会话（未删除）
     */
    @Query("SELECT s FROM CustomerServiceSession s WHERE s.userId = :userId AND s.productId = :productId AND s.sessionStatus = :sessionStatus AND (s.isDeleted = false OR s.isDeleted IS NULL) ORDER BY s.createTime DESC")
    List<CustomerServiceSession> findByUserIdAndProductIdAndStatus(@Param("userId") Long userId, @Param("productId") Long productId, @Param("sessionStatus") Integer sessionStatus);
    
    /**
     * 根据用户ID、会话类型和会话状态查找会话（未删除）
     */
    @Query("SELECT s FROM CustomerServiceSession s WHERE s.userId = :userId AND s.sessionType = :sessionType AND s.sessionStatus = :sessionStatus AND (s.isDeleted = false OR s.isDeleted IS NULL) ORDER BY s.createTime DESC")
    List<CustomerServiceSession> findByUserIdAndSessionTypeAndStatus(@Param("userId") Long userId, @Param("sessionType") Integer sessionType, @Param("sessionStatus") Integer sessionStatus);
    
    /**
     * 根据分配的客服ID查找会话（未删除）
     */
    @Query("SELECT s FROM CustomerServiceSession s WHERE s.assignedAgentId = :assignedAgentId AND (s.isDeleted = false OR s.isDeleted IS NULL) ORDER BY s.createTime DESC")
    List<CustomerServiceSession> findByAssignedAgentId(@Param("assignedAgentId") Long assignedAgentId);
    
    /**
     * 根据会话状态查找会话（未删除）
     */
    List<CustomerServiceSession> findBySessionStatusAndIsDeletedFalseOrderByCreateTimeDesc(Integer sessionStatus);
    
    /**
     * 查找未分配客服的会话（未删除）
     */
    List<CustomerServiceSession> findByAssignedAgentIdIsNullAndSessionStatusAndIsDeletedFalseOrderByCreateTimeAsc(Integer sessionStatus);
    
    /**
     * 根据产品ID查找会话（未删除）
     */
    List<CustomerServiceSession> findByProductIdAndIsDeletedFalseOrderByCreateTimeDesc(Long productId);
    
    /**
     * 根据订单ID查找会话（未删除）
     */
    List<CustomerServiceSession> findByOrderIdAndIsDeletedFalseOrderByCreateTimeDesc(String orderId);
    
    /**
     * 统计用户未读会话数量（未删除）
     */
    @Query("SELECT COUNT(s) FROM CustomerServiceSession s WHERE s.userId = :userId AND s.sessionStatus = 1 AND (s.isDeleted = false OR s.isDeleted IS NULL)")
    long countActiveSessionsByUserId(@Param("userId") Long userId);
    
    /**
     * 统计客服当前会话数量（未删除）
     */
    @Query("SELECT COUNT(s) FROM CustomerServiceSession s WHERE s.assignedAgentId = :assignedAgentId AND s.sessionStatus = 1 AND (s.isDeleted = false OR s.isDeleted IS NULL)")
    long countActiveSessionsByAssignedAgentId(@Param("assignedAgentId") Long assignedAgentId);
    
    /**
     * 查找需要分配客服的会话（优先级高的先分配，未删除）
     */
    @Query("SELECT s FROM CustomerServiceSession s WHERE s.assignedAgentId IS NULL AND s.sessionStatus = 1 AND (s.isDeleted = false OR s.isDeleted IS NULL) ORDER BY s.priority ASC, s.createTime ASC")
    List<CustomerServiceSession> findUnassignedSessionsOrderByPriority();
    
    /**
     * 根据用户ID和产品ID查找会话（未删除）
     */
    @Query("SELECT s FROM CustomerServiceSession s WHERE s.userId = :userId AND s.productId = :productId AND (s.isDeleted = false OR s.isDeleted IS NULL) ORDER BY s.createTime DESC")
    List<CustomerServiceSession> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    /**
     * 根据产品ID和会话状态查找会话（未删除）
     */
    @Query("SELECT s FROM CustomerServiceSession s WHERE s.productId = :productId AND s.sessionStatus = :sessionStatus AND (s.isDeleted = false OR s.isDeleted IS NULL) ORDER BY s.createTime DESC")
    List<CustomerServiceSession> findByProductIdAndStatus(@Param("productId") Long productId, @Param("sessionStatus") Integer sessionStatus);
    
    /**
     * 更新最后消息时间
     */
    @Modifying
    @Query("UPDATE CustomerServiceSession s SET s.lastMessageTime = :lastMessageTime WHERE s.id = :sessionId")
    void updateLastMessageTime(@Param("sessionId") Long sessionId, @Param("lastMessageTime") LocalDateTime lastMessageTime);
} 