package com.agrishopper.service;

import com.agrishopper.model.CustomerServiceMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CustomerServiceMessageService {
    
    /**
     * 发送用户消息
     */
    CustomerServiceMessage sendUserMessage(Long sessionId, Long userId, String content, Integer messageType);
    
    /**
     * 发送客服消息
     */
    CustomerServiceMessage sendAgentMessage(Long sessionId, Long agentId, String content, Integer messageType);
    
    /**
     * 发送系统消息
     */
    CustomerServiceMessage sendSystemMessage(Long sessionId, String content);
    
    /**
     * 根据会话ID查找消息
     */
    List<CustomerServiceMessage> findBySessionId(Long sessionId);
    
    /**
     * 根据会话ID分页查找消息
     */
    Page<CustomerServiceMessage> findBySessionId(Long sessionId, Pageable pageable);
    
    /**
     * 根据会话ID查找未读消息
     */
    List<CustomerServiceMessage> findUnreadMessagesBySessionId(Long sessionId);
    
    /**
     * 标记会话消息为已读
     */
    void markMessagesAsRead(Long sessionId);
    
    /**
     * 统计会话未读消息数量
     */
    long countUnreadMessagesBySessionId(Long sessionId);
    
    /**
     * 统计用户所有会话的未读消息数量
     */
    long countUnreadMessagesByUserId(Long userId);
    
    /**
     * 统计会话中客服未读消息数量（只统计客服消息，不包括AI和系统消息）
     */
    long countUnreadAgentMessagesBySessionId(Long sessionId);
    
    /**
     * 统计用户所有会话的客服未读消息数量（只统计客服消息，不包括AI和系统消息）
     */
    long countUnreadAgentMessagesByUserId(Long userId);
    
    /**
     * 统计会话中非用户未读消息数量（包括客服、AI和系统消息，不包括用户消息）
     */
    long countUnreadNonUserMessagesBySessionId(Long sessionId);
    
    /**
     * 统计用户所有会话的非用户未读消息数量（包括客服、AI和系统消息，不包括用户消息）
     */
    long countUnreadNonUserMessagesByUserId(Long userId);
    
    /**
     * 获取会话最后一条消息
     */
    CustomerServiceMessage getLastMessageBySessionId(Long sessionId);
    
    /**
     * 删除会话的所有消息
     */
    void deleteMessagesBySessionId(Long sessionId);
} 