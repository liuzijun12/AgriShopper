package com.agrishopper.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.Comment;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "商品分类实体类")
@Data
@Entity
@Table(name = "categories")
public class Category {

    @Schema(description = "分类ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("分类ID，自增主键")
    private Long id;

    @Schema(description = "分类名称", example = "农产品")
    @Column(nullable = false, length = 100)
    @Comment("分类名称")
    private String categoryName;

    @Schema(description = "分类描述")
    @Column(columnDefinition = "TEXT")
    @Comment("分类描述")
    private String categoryDescription;

    @Schema(description = "分类图标")
    @Column(length = 500)
    @Comment("分类图标URL")
    private String categoryIcon;

    @Schema(description = "排序权重", example = "1")
    @Column(nullable = false)
    @Comment("排序权重（数字越小越靠前）")
    private Integer sortOrder = 999;

    @Schema(description = "是否启用", example = "true")
    @Column(nullable = false)
    @Comment("是否启用（默认true）")
    private Boolean isEnabled = true;

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