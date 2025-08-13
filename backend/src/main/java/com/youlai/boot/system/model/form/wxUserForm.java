package com.youlai.boot.system.model.form;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

/**
 * 用户表单对象
 *
 * @author liuzijun
 * @since 2025-08-13 15:03
 */
@Getter
@Setter
@Schema(description = "用户表单对象")
public class wxUserForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "不能为空")
    private Integer id;

    @Schema(description = "微信用户唯一ID")
    @NotBlank(message = "微信用户唯一ID不能为空")
    @Size(max=64, message="微信用户唯一ID长度不能超过64个字符")
    private String openid;

    @Schema(description = "微信开放平台ID")
    @Size(max=255, message="微信开放平台ID长度不能超过255个字符")
    private String unionid;

    @Schema(description = "微信昵称")
    @NotBlank(message = "微信昵称不能为空")
    @Size(max=64, message="微信昵称长度不能超过64个字符")
    private String nickname;

    @Schema(description = "头像URL")
    @Size(max=250, message="头像URL长度不能超过250个字符")
    private String avatar;

    @Schema(description = "真实姓名")
    @Size(max=64, message="真实姓名长度不能超过64个字符")
    private String realName;

    @Schema(description = "手机号")
    @Size(max=20, message="手机号长度不能超过20个字符")
    private String phone;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "省份")
    @Size(max=64, message="省份长度不能超过64个字符")
    private String province;

    @Schema(description = "城市")
    @Size(max=64, message="城市长度不能超过64个字符")
    private String city;

    @Schema(description = "地区")
    @Size(max=64, message="地区长度不能超过64个字符")
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
    @NotNull(message = "创建时间不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @NotNull(message = "更新时间不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "删除时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteTime;


}
