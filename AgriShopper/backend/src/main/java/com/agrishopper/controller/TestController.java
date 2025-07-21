package com.agrishopper.controller;

import com.agrishopper.common.ApiResponse;
import com.agrishopper.model.CustomerServiceMessage;
import com.agrishopper.model.CustomerServiceSession;
import com.agrishopper.repository.CustomerServiceMessageRepository;
import com.agrishopper.repository.CustomerServiceSessionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "测试接口", description = "用于测试数据库连接和表结构")
public class TestController {
    
    private final CustomerServiceSessionRepository sessionRepository;
    private final CustomerServiceMessageRepository messageRepository;
    
    @GetMapping("/sessions")
    @Operation(summary = "测试会话表", description = "测试会话表的查询功能")
    public ApiResponse<List<CustomerServiceSession>> testSessions() {
        try {
            log.info("测试会话表查询");
            List<CustomerServiceSession> sessions = sessionRepository.findAll();
            log.info("查询到 {} 个会话", sessions.size());
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            log.error("测试会话表失败", e);
            return ApiResponse.error(500, "测试失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/messages")
    @Operation(summary = "测试消息表", description = "测试消息表的查询功能")
    public ApiResponse<List<CustomerServiceMessage>> testMessages() {
        try {
            log.info("测试消息表查询");
            List<CustomerServiceMessage> messages = messageRepository.findAll();
            log.info("查询到 {} 条消息", messages.size());
            return ApiResponse.success(messages);
        } catch (Exception e) {
            log.error("测试消息表失败", e);
            return ApiResponse.error(500, "测试失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/messages/{sessionId}")
    @Operation(summary = "测试会话消息查询", description = "测试根据会话ID查询消息")
    public ApiResponse<List<CustomerServiceMessage>> testSessionMessages(@PathVariable Long sessionId) {
        try {
            log.info("测试会话消息查询 - 会话ID: {}", sessionId);
            List<CustomerServiceMessage> messages = messageRepository.findBySessionId(sessionId);
            log.info("查询到 {} 条消息", messages.size());
            return ApiResponse.success(messages);
        } catch (Exception e) {
            log.error("测试会话消息查询失败", e);
            return ApiResponse.error(500, "测试失败: " + e.getMessage());
        }
    }
} 