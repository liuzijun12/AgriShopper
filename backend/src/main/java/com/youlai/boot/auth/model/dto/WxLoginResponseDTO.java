package com.youlai.boot.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 微信小程序登录响应对象
 *
 * @author 有来技术团队
 * @since 2.0.0
 */
@Schema(description = "微信小程序登录响应对象")
@Data
@Builder
public class WxLoginResponseDTO {

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "访问令牌")
    private String accessToken;

    @Schema(description = "刷新令牌")
    private String refreshToken;

    @Schema(description = "过期时间(单位：秒)")
    private Integer expiresIn;

    @Schema(description = "微信OpenID")
    private String openid;

    @Schema(description = "微信UnionID")
    private String unionid;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "国家")
    private String country;
} 