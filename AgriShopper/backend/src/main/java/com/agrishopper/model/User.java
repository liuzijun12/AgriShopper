package com.agrishopper.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户实体类
 */
@Schema(description = "用户实体类")
@Data
@Entity
@Table(name = "user")
public class User {
    
    @Schema(description = "用户ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("用户ID，自增主键")
    private Long id;

    @Schema(description = "用户名", example = "zhangsan")
    @Column(nullable = false, unique = true, length = 50)
    @Comment("用户名")
    private String username;

    @Schema(description = "密码", example = "123456")
    @Column(nullable = false, length = 255)
    @Comment("密码")
    private String password;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    @Column(unique = true, length = 100)
    @Comment("邮箱")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    @Column(unique = true, length = 15)
    @Comment("手机号")
    private String phone;

    @Schema(description = "用户角色", example = "customer")
    @Column(nullable = false, length = 20)
    @Comment("用户角色")
    private String role = "customer";

    @Schema(description = "头像URL")
    @Column(length = 255)
    @Comment("头像URL")
    private String avatar;

    @Schema(description = "微信OpenID", example = "open123456789")
    @Column(name = "open_id", length = 255)
    @Comment("微信OpenID（用于小程序登录）")
    private String openId;

    @Schema(description = "微信UnionID", example = "union123456789")
    @Column(name = "union_id", length = 255)
    @Comment("微信UnionID（可选，用于跨小程序的用户标识）")
    private String unionId;

    @Schema(description = "用户的收货地址列表")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("用户的收货地址列表")
    @JsonIgnoreProperties("user")
    private List<Address> addresses = new ArrayList<>();

    @Schema(description = "创建时间", example = "2024-03-21T10:15:30")
    @Column(name = "create_time")
    @Comment("注册时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2024-03-21T10:15:30")
    @Column(name = "update_time")
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否激活", example = "true")
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