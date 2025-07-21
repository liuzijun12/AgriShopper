package com.agrishopper.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    
    private Long sessionId;
    private String content;
    private Integer messageType;
} 