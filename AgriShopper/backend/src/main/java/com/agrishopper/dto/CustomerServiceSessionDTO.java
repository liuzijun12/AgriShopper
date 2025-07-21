package com.agrishopper.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerServiceSessionDTO {
    
    private Long id;
    private String sessionCode;
    private Long userId;
    private Long productId;
    private String orderId;
    private Integer sessionType;
    private Integer sessionStatus;
    private Integer priority;
    private Long assignedAgentId;
    private Boolean autoReplyEnabled;
    private Boolean aiEnabled;
    private String aiModel;
    private LocalDateTime lastMessageTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联信息
    private String userName;
    private String userAvatar;
    private String productName;
    private String productImage;
    private String agentName;
    private String agentAvatar;
    
    // 统计信息
    private Long unreadMessageCount;
    private String lastMessageContent;
} 