package com.agrishopper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;

@Schema(description = "登录响应DTO")
@Data
@Builder
public class LoginResponse {
    
    @Schema(description = "访问令牌")
    private String accessToken;
    
    @Schema(description = "刷新令牌")
    private String refreshToken;
    
    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "过期时间（秒）", example = "3600")
    private Long expiresIn;
    
    @Schema(description = "用户信息")
    private AdminInfo adminInfo;
    
    @Data
    @Builder
    public static class AdminInfo {
        @Schema(description = "管理员ID")
        private Long id;
        
        @Schema(description = "昵称")
        private String nickname;
        
        @Schema(description = "手机号")
        private String phone;
        
        @Schema(description = "性别")
        private Integer gender;
    }
} 