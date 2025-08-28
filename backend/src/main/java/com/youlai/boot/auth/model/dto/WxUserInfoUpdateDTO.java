package com.youlai.boot.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 微信用户信息更新请求参数
 *
 * @author 有来技术团队
 * @since 2.0.0
 */
@Schema(description = "微信用户信息更新请求参数")
@Data
public class WxUserInfoUpdateDTO {

    @Schema(description = "微信OpenID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "openid不能为空")
    private String openid;

    @Schema(description = "微信用户信息")
    private Map<String, Object> userInfo;
} 