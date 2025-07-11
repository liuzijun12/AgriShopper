package com.agrishopper.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("用户ID，自增主键")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("用户名")
    private String username;

    @Column(length = 255)
    @Comment("密码（加密后的密码）")
    private String password;

    @Column(length = 100)
    @Comment("用户邮箱")
    private String email;

    @Column(length = 15)
    @Comment("用户手机号")
    private String phone;

    @Column(length = 50)
    @Comment("用户角色（例如：customer, admin, seller）")
    private String role = "customer";

    @Column(length = 255)
    @Comment("用户头像URL")
    private String avatar;

    @Column(name = "open_id", nullable = false, unique = true, length = 255)
    @Comment("微信OpenID，唯一标识微信用户")
    private String openId;

    @Column(name = "union_id", length = 255)
    @Comment("微信UnionID（可选，用于跨小程序的用户标识）")
    private String unionId;

    @Column(name = "create_time")
    @Comment("注册时间")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @Column(name = "is_active")
    @Comment("用户是否激活")
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
} 