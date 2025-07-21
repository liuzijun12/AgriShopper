package com.agrishopper.controller;

import com.agrishopper.common.ApiResponse;
import com.agrishopper.dto.*;
import com.agrishopper.model.CustomerServiceMessage;
import com.agrishopper.model.CustomerServiceSession;
import com.agrishopper.service.CustomerServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/customer-service")
@RequiredArgsConstructor
@Tag(name = "客服服务", description = "客服对话相关接口")
public class CustomerServiceController {
    
    private final CustomerServiceService customerServiceService;
    
    @PostMapping("/start")
    @Operation(summary = "发起客服咨询", description = "用户发起客服咨询，创建新会话")
    public ApiResponse<CustomerServiceSession> startCustomerService(
            @RequestParam Long userId,
            @RequestBody StartCustomerServiceRequest request) {
        try {
            log.info("用户发起客服咨询 - 用户ID: {}, 请求: {}", userId, request);
            
            CustomerServiceSession session = customerServiceService.startCustomerService(
                    userId,
                    request.getProductId(),
                    request.getOrderId(),
                    request.getSessionType()
            );
            
            return ApiResponse.success(session);
        } catch (Exception e) {
            log.error("发起客服咨询失败", e);
            return ApiResponse.error(500, "发起客服咨询失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/message/send")
    @Operation(summary = "发送消息", description = "用户发送消息到客服")
    public ApiResponse<CustomerServiceMessage> sendUserMessage(
            @RequestParam Long userId,
            @RequestBody SendMessageRequest request) {
        try {
            log.info("用户发送消息 - 用户ID: {}, 会话ID: {}", userId, request.getSessionId());
            
            CustomerServiceMessage message = customerServiceService.sendUserMessage(
                    request.getSessionId(),
                    userId,
                    request.getContent()
            );
            
            return ApiResponse.success(message);
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return ApiResponse.error(500, "发送消息失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/message/reply")
    @Operation(summary = "客服回复", description = "客服回复用户消息")
    public ApiResponse<CustomerServiceMessage> sendAgentReply(
            @RequestParam Long agentId,
            @RequestBody SendMessageRequest request) {
        try {
            log.info("客服回复消息 - 客服ID: {}, 会话ID: {}", agentId, request.getSessionId());
            
            CustomerServiceMessage message = customerServiceService.sendAgentReply(
                    request.getSessionId(),
                    agentId,
                    request.getContent()
            );
            
            return ApiResponse.success(message);
        } catch (Exception e) {
            log.error("客服回复失败", e);
            return ApiResponse.error(500, "客服回复失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/sessions/user/{userId}")
    @Operation(summary = "获取用户会话列表", description = "获取指定用户的所有客服会话")
    public ApiResponse<List<CustomerServiceSession>> getUserSessions(@PathVariable Long userId) {
        try {
            log.info("获取用户会话列表 - 用户ID: {}", userId);
            
            List<CustomerServiceSession> sessions = customerServiceService.getUserSessions(userId);
            
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            log.error("获取用户会话列表失败", e);
            return ApiResponse.error(500, "获取会话列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/sessions/agent/{agentId}")
    @Operation(summary = "获取客服会话列表", description = "获取指定客服的待处理会话")
    public ApiResponse<List<CustomerServiceSession>> getAgentSessions(@PathVariable Long agentId) {
        try {
            log.info("获取客服会话列表 - 客服ID: {}", agentId);
            
            List<CustomerServiceSession> sessions = customerServiceService.getAgentSessions(agentId);
            
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            log.error("获取客服会话列表失败", e);
            return ApiResponse.error(500, "获取客服会话列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/sessions/unassigned")
    @Operation(summary = "获取未分配会话", description = "获取所有未分配客服的会话")
    public ApiResponse<List<CustomerServiceSession>> getUnassignedSessions() {
        try {
            log.info("获取未分配会话列表");
            
            List<CustomerServiceSession> sessions = customerServiceService.getUnassignedSessions();
            
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            log.error("获取未分配会话列表失败", e);
            return ApiResponse.error(500, "获取未分配会话列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/sessions")
    @Operation(summary = "获取所有会话", description = "获取所有客服会话，支持筛选")
    public ApiResponse<List<CustomerServiceSession>> getAllSessions(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer sessionStatus) {
        try {
            log.info("获取所有会话列表 - 用户ID: {}, 产品ID: {}, 状态: {}", userId, productId, sessionStatus);
            
            List<CustomerServiceSession> sessions = customerServiceService.getAllSessions(userId, productId, sessionStatus);
            
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            log.error("获取所有会话列表失败", e);
            return ApiResponse.error(500, "获取会话列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/messages/{sessionId}")
    @Operation(summary = "获取会话消息", description = "获取指定会话的所有消息")
    public ApiResponse<List<CustomerServiceMessage>> getSessionMessages(@PathVariable Long sessionId) {
        try {
            log.info("获取会话消息 - 会话ID: {}", sessionId);
            
            List<CustomerServiceMessage> messages = customerServiceService.getSessionMessages(sessionId);
            
            return ApiResponse.success(messages);
        } catch (Exception e) {
            log.error("获取会话消息失败", e);
            return ApiResponse.error(500, "获取消息列表失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/messages/{sessionId}/read")
    @Operation(summary = "标记消息已读", description = "标记指定会话的消息为已读")
    public ApiResponse<Void> markMessagesAsRead(@PathVariable Long sessionId) {
        try {
            log.info("标记消息已读 - 会话ID: {}", sessionId);
            
            customerServiceService.markMessagesAsRead(sessionId);
            
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("标记消息已读失败", e);
            return ApiResponse.error(500, "标记已读失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/sessions/{sessionId}/assign/{agentId}")
    @Operation(summary = "分配客服", description = "手动分配客服给指定会话")
    public ApiResponse<CustomerServiceSession> assignAgent(
            @PathVariable Long sessionId,
            @PathVariable Long agentId) {
        try {
            log.info("分配客服 - 会话ID: {}, 客服ID: {}", sessionId, agentId);
            
            CustomerServiceSession session = customerServiceService.assignAgent(sessionId, agentId);
            
            return ApiResponse.success(session);
        } catch (Exception e) {
            log.error("分配客服失败", e);
            return ApiResponse.error(500, "分配客服失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/sessions/{sessionId}/end")
    @Operation(summary = "结束会话", description = "结束指定的客服会话")
    public ApiResponse<CustomerServiceSession> endSession(@PathVariable Long sessionId) {
        try {
            log.info("结束会话 - 会话ID: {}", sessionId);
            
            CustomerServiceSession session = customerServiceService.endSession(sessionId);
            
            return ApiResponse.success(session);
        } catch (Exception e) {
            log.error("结束会话失败", e);
            return ApiResponse.error(500, "结束会话失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/stats/user/{userId}/unread")
    @Operation(summary = "获取用户未读消息数", description = "获取指定用户的未读消息数量")
    public ApiResponse<Long> countUserUnreadMessages(@PathVariable Long userId) {
        try {
            log.info("获取用户未读消息数 - 用户ID: {}", userId);
            
            long count = customerServiceService.countUserUnreadMessages(userId);
            
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.error("获取用户未读消息数失败", e);
            return ApiResponse.error(500, "获取未读消息数失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/stats/session/{sessionId}/unread")
    @Operation(summary = "获取会话未读消息数", description = "获取指定会话的未读消息数量")
    public ApiResponse<Long> countSessionUnreadMessages(@PathVariable Long sessionId) {
        try {
            log.info("获取会话未读消息数 - 会话ID: {}", sessionId);
            
            long count = customerServiceService.countSessionUnreadMessages(sessionId);
            
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.error("获取会话未读消息数失败", e);
            return ApiResponse.error(500, "获取会话未读消息数失败: " + e.getMessage());
        }
    }
} 