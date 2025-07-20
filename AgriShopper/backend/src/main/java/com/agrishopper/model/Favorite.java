package com.agrishopper.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.Comment;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户收藏实体类
 * 用于存储用户收藏的商品信息
 */
@Schema(description = "用户收藏实体类")
@Data
@Entity
@Table(name = "favorites")
public class Favorite {

    @Schema(description = "收藏ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("收藏ID，自增主键")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    @Column(name = "userId", nullable = false)
    @Comment("用户ID（关联wxuser表）")
    private Long userId;

    @Schema(description = "商品编号", example = "507233")
    @Column(name = "productCode", nullable = false, length = 50)
    @Comment("商品编号（关联products表的productCode）")
    private String productCode;

    @Schema(description = "创建时间")
    @Column(name = "createTime", nullable = false, updatable = false)
    @Comment("创建时间（自动生成，不可修改）")
    private LocalDateTime createTime = LocalDateTime.now();

    @Schema(description = "更新时间")
    @Column(name = "updateTime", nullable = false)
    @Comment("更新时间（自动更新）")
    private LocalDateTime updateTime = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 