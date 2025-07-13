package com.agrishopper.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "微信用户实体类")
@Data
@Entity
@Table(name = "wxUser", uniqueConstraints = {@UniqueConstraint(columnNames = "openid")})
public class WxUser {

    @Schema(description = "主键ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键，自增ID")
    private Long id;

    @Schema(description = "微信openid，用户在当前小程序下的唯一标识", example = "o7GHu5Fxxxxxx")
    @Column(nullable = false, unique = true, length = 64)
    @Comment("微信openid，用户在当前小程序下的唯一标识")
    private String openid;

    @Schema(description = "微信unionid，用户在开放平台下的唯一标识（如有绑定）", example = "o7GHu5FxxxxxxUnion")
    @Column(length = 64)
    @Comment("微信unionid，用户在开放平台下的唯一标识（如有绑定）")
    private String unionid;

    @Schema(description = "微信昵称", example = "小明")
    @Column(length = 64)
    @Comment("微信昵称")
    private String nickname;

    @Schema(description = "微信头像URL", example = "https://wx.qlogo.cn/xxx.jpg")
    @Column(length = 255)
    @Comment("微信头像URL")
    private String avatar;

    @Schema(description = "性别（male/female/unknown）", example = "male")
    @Column(length = 10)
    @Comment("性别")
    private String gender;

    @Schema(description = "手机号", example = "13800138000")
    @Column(length = 20)
    @Comment("手机号")
    private String phone;

    @Schema(description = "国家", example = "中国")
    @Column(length = 32)
    @Comment("国家")
    private String country;

    @Schema(description = "省份", example = "广东省")
    @Column(length = 32)
    @Comment("省份")
    private String province;

    @Schema(description = "城市", example = "深圳市")
    @Column(length = 32)
    @Comment("城市")
    private String city;

    @Schema(description = "创建时间")
    @Column(nullable = false, updatable = false)
    @Comment("创建时间（自动生成，不可修改）")
    private LocalDateTime createTime = LocalDateTime.now();

    @Schema(description = "更新时间")
    @Column(nullable = false)
    @Comment("更新时间（自动更新）")
    private LocalDateTime updateTime = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 