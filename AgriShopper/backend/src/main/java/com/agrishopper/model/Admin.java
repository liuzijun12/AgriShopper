package com.agrishopper.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.Comment;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "管理员用户实体类")
@Data
@Entity
@Table(name = "admins")
public class Admin {

    @Schema(description = "管理员ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("管理员ID，自增主键")
    private Long id;

    @Schema(description = "昵称", example = "管理员")
    @Column(nullable = false, length = 50)
    @Comment("昵称")
    private String nickname;

    @Schema(description = "密码", example = "password")
    @Column(nullable = false, length = 255)
    @Comment("密码")
    private String password;

    @Schema(description = "手机号", example = "13800138000")
    @Column(nullable = false, unique = true, length = 20)
    @Comment("手机号（唯一）")
    private String phone;

    @Schema(description = "性别", example = "1")
    @Column(nullable = false)
    @Comment("性别（0-未知，1-男，2-女）")
    private Integer gender = 0;

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