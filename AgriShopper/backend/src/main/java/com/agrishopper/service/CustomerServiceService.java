package com.agrishopper.service;

import com.agrishopper.model.CustomerServiceSession;
import com.agrishopper.model.CustomerServiceMessage;
import java.util.List;

public interface CustomerServiceService {
    
    /**
     * 用户发起客服咨询
     */
    CustomerServiceSession startCustomerService(Long userId, Long productId, String orderId, Integer sessionType);
    
    /**
     * 用户发送消息
     */
    CustomerServiceMessage sendUserMessage(Long sessionId, Long userId, String content);
    
    /**
     * 客服回复消息
     */
    CustomerServiceMessage sendAgentReply(Long sessionId, Long agentId, String content);
    
    /**
     * 自动分配客服
     */
    CustomerServiceSession autoAssignAgent(Long sessionId);
    
    /**
     * 手动分配客服
     */
    CustomerServiceSession assignAgent(Long sessionId, Long agentId);
    
    /**
     * 获取用户的所有会话
     */
    List<CustomerServiceSession> getUserSessions(Long userId);
    
    /**
     * 获取会话的所有消息
     */
    List<CustomerServiceMessage> getSessionMessages(Long sessionId);
    
    /**
     * 标记消息为已读
     */
    void markMessagesAsRead(Long sessionId);
    
    /**
     * 结束会话
     */
    CustomerServiceSession endSession(Long sessionId);
    
    /**
     * 获取客服的待处理会话
     */
    List<CustomerServiceSession> getAgentSessions(Long agentId);
    
    /**
     * 获取未分配的会话
     */
    List<CustomerServiceSession> getUnassignedSessions();
    
    /**
     * 获取所有会话（支持筛选）
     */
    List<CustomerServiceSession> getAllSessions(Long userId, Long productId, Integer sessionStatus);
    
    /**
     * 统计用户未读消息数量
     */
    long countUserUnreadMessages(Long userId);
    
    /**
     * 统计会话未读消息数量
     */
    long countSessionUnreadMessages(Long sessionId);
} 