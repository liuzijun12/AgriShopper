package com.agrishopper.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerServiceMessageDTO {
    
    private Long id;
    private Long sessionId;
    private Integer senderType;
    private Long senderId;
    private Integer messageType;
    private String messageContent;
    private String messageMediaUrl;
    private Integer messageDuration;
    private Boolean isRead;
    private LocalDateTime readTime;
    private Boolean aiGenerated;
    private BigDecimal aiConfidence;
    private String aiModel;
    private LocalDateTime createTime;
    
    // 发送者信息
    private String senderName;
    private String senderAvatar;
    
    // 消息类型描述
    private String messageTypeDesc;
    private String senderTypeDesc;
} 