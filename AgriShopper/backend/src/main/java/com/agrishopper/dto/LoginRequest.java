package com.agrishopper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "登录请求DTO")
@Data
public class LoginRequest {
    
    @Schema(description = "登录账号（手机号或昵称）", example = "18330177876")
    private String account;
    
    @Schema(description = "密码", example = "password")
    private String password;
    
    @Schema(description = "记住我", example = "false")
    private Boolean rememberMe = false;
} 