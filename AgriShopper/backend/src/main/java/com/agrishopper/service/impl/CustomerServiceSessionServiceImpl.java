package com.agrishopper.service.impl;

import com.agrishopper.common.CustomerServiceConstants;
import com.agrishopper.model.CustomerServiceSession;
import com.agrishopper.repository.CustomerServiceSessionRepository;
import com.agrishopper.service.CustomerServiceSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceSessionServiceImpl implements CustomerServiceSessionService {
    
    private final CustomerServiceSessionRepository sessionRepository;
    
    @Override
    public CustomerServiceSession createSession(Long userId, Long productId, String orderId, Integer sessionType) {
        log.info("创建客服会话 - 用户ID: {}, 产品ID: {}, 订单ID: {}, 会话类型: {}", userId, productId, orderId, sessionType);
        
        CustomerServiceSession session = new CustomerServiceSession();
        session.setSessionCode(generateSessionCode());
        session.setUserId(userId);
        session.setProductId(productId);
        session.setOrderId(orderId);
        session.setSessionType(sessionType);
        session.setSessionStatus(CustomerServiceConstants.SessionStatus.ACTIVE);
        session.setPriority(CustomerServiceConstants.Priority.MEDIUM);
        session.setAutoReplyEnabled(true);
        session.setAiEnabled(false); // 暂时关闭AI功能
        
        CustomerServiceSession savedSession = sessionRepository.save(session);
        log.info("客服会话创建成功 - 会话ID: {}, 会话编码: {}", savedSession.getId(), savedSession.getSessionCode());
        
        return savedSession;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerServiceSession> findById(Long id) {
        return sessionRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerServiceSession> findBySessionCode(String sessionCode) {
        return sessionRepository.findBySessionCode(sessionCode);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> findByUserId(Long userId) {
        return sessionRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> findActiveSessionsByUserId(Long userId) {
        return sessionRepository.findByUserIdAndStatus(userId, CustomerServiceConstants.SessionStatus.ACTIVE);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> findActiveSessionsByUserIdAndProductId(Long userId, Long productId) {
        return sessionRepository.findByUserIdAndProductIdAndStatus(userId, productId, CustomerServiceConstants.SessionStatus.ACTIVE);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> findActiveSessionsByUserIdAndSessionType(Long userId, Integer sessionType) {
        return sessionRepository.findByUserIdAndSessionTypeAndStatus(userId, sessionType, CustomerServiceConstants.SessionStatus.ACTIVE);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> findByAssignedAgentId(Long agentId) {
        return sessionRepository.findByAssignedAgentId(agentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> findUnassignedSessions() {
        return sessionRepository.findUnassignedSessionsOrderByPriority();
    }
    
    @Override
    public CustomerServiceSession assignAgent(Long sessionId, Long agentId) {
        log.info("分配客服 - 会话ID: {}, 客服ID: {}", sessionId, agentId);
        
        Optional<CustomerServiceSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("会话不存在 - 会话ID: " + sessionId);
        }
        
        CustomerServiceSession session = sessionOpt.get();
        session.setAssignedAgentId(agentId);
        session.setUpdateTime(LocalDateTime.now());
        
        CustomerServiceSession savedSession = sessionRepository.save(session);
        log.info("客服分配成功 - 会话ID: {}, 客服ID: {}", savedSession.getId(), savedSession.getAssignedAgentId());
        
        return savedSession;
    }
    
    @Override
    public CustomerServiceSession updateSessionStatus(Long sessionId, Integer status) {
        log.info("更新会话状态 - 会话ID: {}, 状态: {}", sessionId, status);
        
        Optional<CustomerServiceSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("会话不存在 - 会话ID: " + sessionId);
        }
        
        CustomerServiceSession session = sessionOpt.get();
        session.setSessionStatus(status);
        session.setUpdateTime(LocalDateTime.now());
        
        CustomerServiceSession savedSession = sessionRepository.save(session);
        log.info("会话状态更新成功 - 会话ID: {}, 状态: {}", savedSession.getId(), savedSession.getSessionStatus());
        
        return savedSession;
    }
    
    @Override
    public CustomerServiceSession endSession(Long sessionId) {
        log.info("结束会话 - 会话ID: {}", sessionId);
        return updateSessionStatus(sessionId, CustomerServiceConstants.SessionStatus.ENDED);
    }
    
    @Override
    public void updateLastMessageTime(Long sessionId) {
        sessionRepository.updateLastMessageTime(sessionId, LocalDateTime.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countActiveSessionsByUserId(Long userId) {
        return sessionRepository.countActiveSessionsByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countActiveSessionsByAgentId(Long agentId) {
        return sessionRepository.countActiveSessionsByAssignedAgentId(agentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> findByUserIdAndProductId(Long userId, Long productId) {
        return sessionRepository.findByUserIdAndProductId(userId, productId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> findByUserIdAndProductIdAndStatus(Long userId, Long productId, Integer status) {
        return sessionRepository.findByUserIdAndProductIdAndStatus(userId, productId, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> findByUserIdAndStatus(Long userId, Integer status) {
        return sessionRepository.findByUserIdAndStatus(userId, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> findByProductIdAndStatus(Long productId, Integer status) {
        return sessionRepository.findByProductIdAndStatus(productId, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> findByProductId(Long productId) {
        return sessionRepository.findByProductIdAndIsDeletedFalseOrderByCreateTimeDesc(productId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> findBySessionStatus(Integer status) {
        return sessionRepository.findBySessionStatusAndIsDeletedFalseOrderByCreateTimeDesc(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerServiceSession> findAllActiveSessions() {
        return sessionRepository.findBySessionStatusAndIsDeletedFalseOrderByCreateTimeDesc(
            CustomerServiceConstants.SessionStatus.ACTIVE
        );
    }
    
    public String generateSessionCode() {
        return "CS" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + 
               String.format("%04d", (int)(Math.random() * 10000));
    }
} 