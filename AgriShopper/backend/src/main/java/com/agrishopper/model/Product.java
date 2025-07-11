package com.agrishopper.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.Comment;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "商品实体类")
@Data
@Entity
@Table(name = "products")
public class Product {

    @Schema(description = "商品ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("商品ID，自增主键")
    private Long id;

    @Schema(description = "商品名称", example = "有机大米")
    @Column(nullable = false, length = 255)
    @Comment("商品名称")
    private String name;

    @Schema(description = "商品价格", example = "29.90")
    @Column(nullable = false)
    @Comment("商品价格")
    private BigDecimal price;

    @Schema(description = "商品描述")
    @Column(columnDefinition = "TEXT")
    @Comment("商品描述")
    private String description;

    @Schema(description = "商品照片URL")
    @Column(length = 500)
    @Comment("商品照片（路径或URL）")
    private String photo;

    @Schema(description = "商品类别", example = "粮食")
    @Column(nullable = false, length = 100)
    @Comment("商品类别")
    private String category;

    @Schema(description = "商品库存", example = "100")
    @Column(nullable = false)
    @Comment("商品库存")
    private Integer stock = 0;

    @Schema(description = "商品上架时间")
    @Column(nullable = false)
    @Comment("商品上架时间")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Schema(description = "是否上架", example = "true")
    @Column(nullable = false)
    @Comment("是否上架")
    private Boolean isAvailable = true;

    @Schema(description = "商品品牌", example = "金龙鱼")
    @Column(length = 100)
    @Comment("商品品牌")
    private String brand;

    @Schema(description = "商品折扣", example = "0.95")
    @Column(precision = 5, scale = 2)
    @Comment("商品折扣")
    private BigDecimal discount = BigDecimal.ZERO;

    @Schema(description = "商品销售数量", example = "50")
    @Column(nullable = false)
    @Comment("商品销售数量")
    private Integer salesCount = 0;

    @Schema(description = "商品平均评分", example = "4.5")
    @Column(precision = 3, scale = 2)
    @Comment("商品平均评分")
    private BigDecimal rating = BigDecimal.ZERO;
}
