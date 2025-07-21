package com.agrishopper.service.impl;

import com.agrishopper.common.CustomerServiceConstants;
import com.agrishopper.model.CustomerServiceSession;
import com.agrishopper.model.CustomerServiceMessage;
import com.agrishopper.repository.AdminRepository;
import com.agrishopper.service.CustomerServiceService;
import com.agrishopper.service.CustomerServiceSessionService;
import com.agrishopper.service.CustomerServiceMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceServiceImpl implements CustomerServiceService {
    
    private final CustomerServiceSessionService sessionService;
    private final CustomerServiceMessageService messageService;
    private final AdminRepository adminRepository;
    
    @Override
    public CustomerServiceSession startCustomerService(Long userId, Long productId, String orderId, Integer sessionType) {
        log.info("用户发起客服咨询 - 用户ID: {}, 产品ID: {}, 订单ID: {}, 会话类型: {}", userId, productId, orderId, sessionType);
        
        // 检查是否已存在相同用户和商品的活跃会话
        if (productId != null) {
            List<CustomerServiceSession> existingSessions = sessionService.findActiveSessionsByUserIdAndProductId(userId, productId);
            if (!existingSessions.isEmpty()) {
                log.info("找到现有会话 - 用户ID: {}, 产品ID: {}, 会话ID: {}", userId, productId, existingSessions.get(0).getId());
                return existingSessions.get(0);
            }
        }
        
        // 检查是否已存在相同用户的活跃会话（非产品咨询）
        if (productId == null) {
            List<CustomerServiceSession> existingSessions = sessionService.findActiveSessionsByUserIdAndSessionType(userId, sessionType);
            if (!existingSessions.isEmpty()) {
                log.info("找到现有会话 - 用户ID: {}, 会话类型: {}, 会话ID: {}", userId, sessionType, existingSessions.get(0).getId());
                return existingSessions.get(0);
            }
        }
        
        // 创建新会话
        CustomerServiceSession session = sessionService.createSession(userId, productId, orderId, sessionType);
        
        log.info("客服咨询启动成功 - 会话ID: {}", session.getId());
        return session;
    }
    
    @Override
    public CustomerServiceMessage sendUserMessage(Long sessionId, Long userId, String content) {
        log.info("用户发送消息 - 会话ID: {}, 用户ID: {}", sessionId, userId);
        
        // 检查会话是否存在
        Optional<CustomerServiceSession> sessionOpt = sessionService.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("会话不存在 - 会话ID: " + sessionId);
        }
        
        // 发送用户消息
        CustomerServiceMessage message = messageService.sendUserMessage(sessionId, userId, content, CustomerServiceConstants.MessageType.TEXT);
        
        log.info("用户消息发送成功 - 消息ID: {}", message.getId());
        return message;
    }
    
    @Override
    public CustomerServiceMessage sendAgentReply(Long sessionId, Long agentId, String content) {
        log.info("客服回复消息 - 会话ID: {}, 客服ID: {}", sessionId, agentId);
        
        // 检查会话是否存在
        Optional<CustomerServiceSession> sessionOpt = sessionService.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("会话不存在 - 会话ID: " + sessionId);
        }
        
        CustomerServiceSession session = sessionOpt.get();
        
        // 检查客服是否有权限回复此会话
        if (!agentId.equals(session.getAssignedAgentId())) {
            throw new RuntimeException("客服无权限回复此会话 - 会话ID: " + sessionId + ", 客服ID: " + agentId);
        }
        
        // 发送客服消息
        CustomerServiceMessage message = messageService.sendAgentMessage(sessionId, agentId, content, CustomerServiceConstants.MessageType.TEXT);
        
        log.info("客服回复发送成功 - 消息ID: {}", message.getId());
        return message;
    }
    
    @Override
    public CustomerServiceSession autoAssignAgent(Long sessionId) {
        log.info("自动分配客服 - 会话ID: {}", sessionId);
        
        // 查找可用的客服（这里简化处理，实际应该根据客服工作状态和负载来分配）
        List<Long> availableAgents = adminRepository.findAll().stream()
                .map(admin -> admin.getId())
                .toList();
        
        if (availableAgents.isEmpty()) {
            log.warn("没有可用的客服 - 会话ID: {}", sessionId);
            return sessionService.findById(sessionId).orElse(null);
        }
        
        // 简单的负载均衡：选择当前会话数最少的客服
        Long selectedAgentId = availableAgents.get(0);
        long minSessions = Long.MAX_VALUE;
        
        for (Long agentId : availableAgents) {
            long sessionCount = sessionService.countActiveSessionsByAgentId(agentId);
            if (sessionCount < minSessions) {
                minSessions = sessionCount;
                selectedAgentId = agentId;
            }
        }
        
        // 分配客服
        CustomerServiceSession session = sessionService.assignAgent(sessionId, selectedAgentId);
        
        log.info("客服自动分配成功 - 会话ID: {}, 客服ID: {}", sessionId, selectedAgentId);
        return session;
    }
    
    @Override
    public CustomerServiceSession assignAgent(Long sessionId, Long agentId) {
        log.info("手动分配客服 - 会话ID: {}, 客服ID: {}", sessionId, agentId);
        
        // 检查客服是否存在
        if (!adminRepository.existsById(agentId)) {
            throw new RuntimeException("客服不存在 - 客服ID: " + agentId);
        }
        
        // 分配客服
        CustomerServiceSession session = sessionService.assignAgent(sessionId, agentId);
        
        log.info("客服手动分配成功 - 会话ID: {}, 客服ID: {}", sessionId, agentId);
        return session;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> getUserSessions(Long userId) {
        return sessionService.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceMessage> getSessionMessages(Long sessionId) {
        log.info("开始获取会话消息 - 会话ID: {}", sessionId);
        
        try {
            // 首先检查会话是否存在
            Optional<CustomerServiceSession> sessionOpt = sessionService.findById(sessionId);
            if (sessionOpt.isEmpty()) {
                log.error("会话不存在 - 会话ID: {}", sessionId);
                throw new RuntimeException("会话不存在 - 会话ID: " + sessionId);
            }
            
            // 获取消息列表
            List<CustomerServiceMessage> messages = messageService.findBySessionId(sessionId);
            log.info("成功获取会话消息 - 会话ID: {}, 消息数量: {}", sessionId, messages.size());
            
            return messages;
        } catch (Exception e) {
            log.error("获取会话消息失败 - 会话ID: {}, 错误: {}", sessionId, e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public void markMessagesAsRead(Long sessionId) {
        messageService.markMessagesAsRead(sessionId);
    }
    
    @Override
    public CustomerServiceSession endSession(Long sessionId) {
        log.info("结束会话 - 会话ID: {}", sessionId);
        
        // 结束会话
        CustomerServiceSession session = sessionService.endSession(sessionId);
        
        log.info("会话结束成功 - 会话ID: {}", sessionId);
        return session;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> getAgentSessions(Long agentId) {
        return sessionService.findByAssignedAgentId(agentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> getUnassignedSessions() {
        return sessionService.findUnassignedSessions();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> getAllSessions(Long userId, Long productId, Integer sessionStatus) {
        log.info("获取所有会话 - 用户ID: {}, 产品ID: {}, 状态: {}", userId, productId, sessionStatus);
        
        List<CustomerServiceSession> sessions;
        
        if (userId != null && productId != null && sessionStatus != null) {
            sessions = sessionService.findByUserIdAndProductIdAndStatus(userId, productId, sessionStatus);
        } else if (userId != null && productId != null) {
            sessions = sessionService.findByUserIdAndProductId(userId, productId);
        } else if (userId != null && sessionStatus != null) {
            sessions = sessionService.findByUserIdAndStatus(userId, sessionStatus);
        } else if (productId != null && sessionStatus != null) {
            sessions = sessionService.findByProductIdAndStatus(productId, sessionStatus);
        } else if (userId != null) {
            sessions = sessionService.findByUserId(userId);
        } else if (productId != null) {
            sessions = sessionService.findByProductId(productId);
        } else if (sessionStatus != null) {
            sessions = sessionService.findBySessionStatus(sessionStatus);
        } else {
            sessions = sessionService.findAllActiveSessions();
        }
        
        log.info("返回会话数量: {}", sessions.size());
        return sessions;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUserUnreadMessages(Long userId) {
        return messageService.countUnreadMessagesByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countSessionUnreadMessages(Long sessionId) {
        return messageService.countUnreadMessagesBySessionId(sessionId);
    }
} 