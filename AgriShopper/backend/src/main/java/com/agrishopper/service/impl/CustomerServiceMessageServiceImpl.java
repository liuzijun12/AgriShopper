package com.agrishopper.service.impl;

import com.agrishopper.common.CustomerServiceConstants;
import com.agrishopper.model.CustomerServiceMessage;
import com.agrishopper.repository.CustomerServiceMessageRepository;
import com.agrishopper.service.CustomerServiceMessageService;
import com.agrishopper.service.CustomerServiceSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceMessageServiceImpl implements CustomerServiceMessageService {
    
    private final CustomerServiceMessageRepository messageRepository;
    private final CustomerServiceSessionService sessionService;
    
    @Override
    public CustomerServiceMessage sendUserMessage(Long sessionId, Long userId, String content, Integer messageType) {
        log.info("发送用户消息 - 会话ID: {}, 用户ID: {}, 消息类型: {}", sessionId, userId, messageType);
        
        CustomerServiceMessage message = new CustomerServiceMessage();
        message.setSessionId(sessionId);
        message.setSenderType(CustomerServiceConstants.SenderType.USER);
        message.setSenderId(userId);
        message.setMessageType(messageType != null ? messageType : CustomerServiceConstants.MessageType.TEXT);
        message.setMessageContent(content);
        message.setAiGenerated(false);
        
        CustomerServiceMessage savedMessage = messageRepository.save(message);
        
        // 更新会话的最后消息时间
        sessionService.updateLastMessageTime(sessionId);
        
        log.info("用户消息发送成功 - 消息ID: {}", savedMessage.getId());
        return savedMessage;
    }
    
    @Override
    public CustomerServiceMessage sendAgentMessage(Long sessionId, Long agentId, String content, Integer messageType) {
        log.info("发送客服消息 - 会话ID: {}, 客服ID: {}, 消息类型: {}", sessionId, agentId, messageType);
        
        CustomerServiceMessage message = new CustomerServiceMessage();
        message.setSessionId(sessionId);
        message.setSenderType(CustomerServiceConstants.SenderType.AGENT);
        message.setSenderId(agentId);
        message.setMessageType(messageType != null ? messageType : CustomerServiceConstants.MessageType.TEXT);
        message.setMessageContent(content);
        message.setAiGenerated(false);
        
        CustomerServiceMessage savedMessage = messageRepository.save(message);
        
        // 更新会话的最后消息时间
        sessionService.updateLastMessageTime(sessionId);
        
        log.info("客服消息发送成功 - 消息ID: {}", savedMessage.getId());
        return savedMessage;
    }
    
    @Override
    public CustomerServiceMessage sendSystemMessage(Long sessionId, String content) {
        log.info("发送系统消息 - 会话ID: {}", sessionId);
        
        CustomerServiceMessage message = new CustomerServiceMessage();
        message.setSessionId(sessionId);
        message.setSenderType(CustomerServiceConstants.SenderType.SYSTEM);
        message.setMessageType(CustomerServiceConstants.MessageType.SYSTEM_NOTIFICATION);
        message.setMessageContent(content);
        message.setAiGenerated(false);
        
        CustomerServiceMessage savedMessage = messageRepository.save(message);
        
        // 更新会话的最后消息时间
        sessionService.updateLastMessageTime(sessionId);
        
        log.info("系统消息发送成功 - 消息ID: {}", savedMessage.getId());
        return savedMessage;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceMessage> findBySessionId(Long sessionId) {
        return messageRepository.findBySessionId(sessionId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CustomerServiceMessage> findBySessionId(Long sessionId, Pageable pageable) {
        return messageRepository.findBySessionIdPageable(sessionId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceMessage> findUnreadMessagesBySessionId(Long sessionId) {
        return messageRepository.findUnreadBySessionId(sessionId);
    }
    
    @Override
    public void markMessagesAsRead(Long sessionId) {
        log.info("标记消息为已读 - 会话ID: {}", sessionId);
        messageRepository.markMessagesAsReadBySessionId(sessionId, LocalDateTime.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUnreadMessagesBySessionId(Long sessionId) {
        return messageRepository.countUnreadMessagesBySessionId(sessionId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUnreadMessagesByUserId(Long userId) {
        return messageRepository.countUnreadMessagesByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUnreadAgentMessagesBySessionId(Long sessionId) {
        return messageRepository.countUnreadAgentMessagesBySessionId(sessionId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUnreadAgentMessagesByUserId(Long userId) {
        return messageRepository.countUnreadAgentMessagesByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUnreadNonUserMessagesBySessionId(Long sessionId) {
        return messageRepository.countUnreadNonUserMessagesBySessionId(sessionId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUnreadNonUserMessagesByUserId(Long userId) {
        return messageRepository.countUnreadNonUserMessagesByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CustomerServiceMessage getLastMessageBySessionId(Long sessionId) {
        Pageable pageable = Pageable.ofSize(1);
        List<CustomerServiceMessage> messages = messageRepository.findLastMessageBySessionId(sessionId, pageable);
        return messages.isEmpty() ? null : messages.get(0);
    }
    
    @Override
    public void deleteMessagesBySessionId(Long sessionId) {
        log.info("软删除会话消息 - 会话ID: {}", sessionId);
        messageRepository.softDeleteBySessionId(sessionId);
    }
} 