package com.youlai.boot.system.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 用户视图对象
 *
 * @author liuzijun
 * @since 2025-08-13 15:03
 */
@Getter
@Setter
@Schema( description = "用户视图对象")
public class wxUserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    @Schema(description = "微信用户唯一ID")
    private String openid;
    @Schema(description = "微信开放平台ID")
    private String unionid;
    @Schema(description = "微信昵称")
    private String nickname;
    @Schema(description = "头像URL")
    private String avatar;
    @Schema(description = "真实姓名")
    private String realName;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "性别")
    private Integer gender;
    @Schema(description = "省份")
    private String province;
    @Schema(description = "城市")
    private String city;
    @Schema(description = "地区")
    private String district;
    @Schema(description = "是否是管理员")
    private Integer isManager;
    @Schema(description = "是否是超级管理员")
    private Integer isSupermanager;
    @Schema(description = "账户余额")
    private BigDecimal balance;
    @Schema(description = "是否软删除")
    private Integer isDeleted;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "删除时间")
    private LocalDateTime deleteTime;
}
