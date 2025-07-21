package com.agrishopper.service;

import com.agrishopper.model.CustomerServiceSession;
import java.util.List;
import java.util.Optional;

public interface CustomerServiceSessionService {
    
    /**
     * 创建新的客服会话
     */
    CustomerServiceSession createSession(Long userId, Long productId, String orderId, Integer sessionType);
    
    /**
     * 根据ID查找会话
     */
    Optional<CustomerServiceSession> findById(Long id);
    
    /**
     * 根据会话编码查找会话
     */
    Optional<CustomerServiceSession> findBySessionCode(String sessionCode);
    
    /**
     * 根据用户ID查找所有会话
     */
    List<CustomerServiceSession> findByUserId(Long userId);
    
    /**
     * 根据用户ID查找活跃会话
     */
    List<CustomerServiceSession> findActiveSessionsByUserId(Long userId);
    
    /**
     * 根据用户ID和产品ID查找活跃会话
     */
    List<CustomerServiceSession> findActiveSessionsByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 根据用户ID和会话类型查找活跃会话
     */
    List<CustomerServiceSession> findActiveSessionsByUserIdAndSessionType(Long userId, Integer sessionType);
    
    /**
     * 根据客服ID查找分配的会话
     */
    List<CustomerServiceSession> findByAssignedAgentId(Long agentId);
    
    /**
     * 查找未分配客服的会话
     */
    List<CustomerServiceSession> findUnassignedSessions();
    
    /**
     * 分配客服给会话
     */
    CustomerServiceSession assignAgent(Long sessionId, Long agentId);
    
    /**
     * 更新会话状态
     */
    CustomerServiceSession updateSessionStatus(Long sessionId, Integer status);
    
    /**
     * 结束会话
     */
    CustomerServiceSession endSession(Long sessionId);
    
    /**
     * 更新最后消息时间
     */
    void updateLastMessageTime(Long sessionId);
    
    /**
     * 根据用户ID和产品ID查找会话
     */
    List<CustomerServiceSession> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 根据用户ID、产品ID和状态查找会话
     */
    List<CustomerServiceSession> findByUserIdAndProductIdAndStatus(Long userId, Long productId, Integer status);
    
    /**
     * 根据用户ID和状态查找会话
     */
    List<CustomerServiceSession> findByUserIdAndStatus(Long userId, Integer status);
    
    /**
     * 根据产品ID和状态查找会话
     */
    List<CustomerServiceSession> findByProductIdAndStatus(Long productId, Integer status);
    
    /**
     * 根据产品ID查找会话
     */
    List<CustomerServiceSession> findByProductId(Long productId);
    
    /**
     * 根据状态查找会话
     */
    List<CustomerServiceSession> findBySessionStatus(Integer status);
    
    /**
     * 查找所有活跃会话
     */
    List<CustomerServiceSession> findAllActiveSessions();
    
    /**
     * 统计用户活跃会话数量
     */
    long countActiveSessionsByUserId(Long userId);
    
    /**
     * 统计客服当前会话数量
     */
    long countActiveSessionsByAgentId(Long agentId);
    
    /**
     * 生成会话编码
     */
    String generateSessionCode();
} 