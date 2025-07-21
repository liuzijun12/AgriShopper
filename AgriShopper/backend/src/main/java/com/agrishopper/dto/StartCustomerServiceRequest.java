package com.agrishopper.dto;

import lombok.Data;

@Data
public class StartCustomerServiceRequest {
    
    private Long productId;
    private String orderId;
    private Integer sessionType;
} 