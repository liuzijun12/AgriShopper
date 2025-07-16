package com.agrishopper.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.Comment;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "购物车实体类")
@Data
@Entity
@Table(name = "shoppingcart")
public class ShoppingCart {

    @Schema(description = "购物车项ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("购物车项ID，自增主键")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    @Column(nullable = false)
    @Comment("用户ID（关联wxuser表）")
    private Long userId;

    @Schema(description = "商品ID", example = "1")
    @Column(nullable = false)
    @Comment("商品ID（关联products表）")
    private Long productId;

    @Schema(description = "商品数量", example = "2")
    @Column(nullable = false)
    @Comment("商品数量（默认1）")
    private Integer quantity = 1;

    @Schema(description = "商品单价", example = "29.90")
    @Column(nullable = false, precision = 10, scale = 2)
    @Comment("商品单价（精确到小数点后2位）")
    private BigDecimal unitPrice;

    @Schema(description = "小计金额", example = "59.80")
    @Column(nullable = false, precision = 10, scale = 2)
    @Comment("小计金额（单价×数量）")
    private BigDecimal subtotal;

    @Schema(description = "是否选中", example = "true")
    @Column(nullable = false)
    @Comment("是否选中（默认true）")
    private Boolean isSelected = true;

    @Schema(description = "是否删除", example = "false")
    @Column(nullable = false)
    @Comment("是否删除（默认false，1-正常，0-已删除）")
    private Boolean isDeleted = false;

    @Schema(description = "创建时间")
    @Column(nullable = false, updatable = false)
    @Comment("创建时间（自动生成，不可修改）")
    private LocalDateTime createTime = LocalDateTime.now();

    @Schema(description = "更新时间")
    @Column(nullable = false)
    @Comment("更新时间（自动更新）")
    private LocalDateTime updateTime = LocalDateTime.now();

    // 关联的商品信息（非数据库字段，用于查询时填充）
    @Transient
    @Schema(description = "商品信息")
    private Product product;

    // 关联的用户信息（非数据库字段，用于查询时填充）
    @Transient
    @Schema(description = "用户信息")
    private WxUser user;

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        // 自动计算小计
        if (this.unitPrice != null && this.quantity != null) {
            this.subtotal = this.unitPrice.multiply(new BigDecimal(this.quantity));
        }
    }

    // 手动计算小计的方法
    public void calculateSubtotal() {
        if (this.unitPrice != null && this.quantity != null) {
            this.subtotal = this.unitPrice.multiply(new BigDecimal(this.quantity));
        }
    }
} 