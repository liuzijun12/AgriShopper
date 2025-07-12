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

    @Schema(description = "商品编码", example = "P001")
    @Column(nullable = false, unique = true, length = 50)
    @Comment("商品编码（唯一）")
    private String productCode;

    @Schema(description = "商品名称", example = "有机大米")
    @Column(nullable = false, length = 255)
    @Comment("商品名称")
    private String productName;

    @Schema(description = "商品描述")
    @Column(columnDefinition = "TEXT")
    @Comment("商品描述（文本类型）")
    private String productDescription;

    @Schema(description = "所属分类ID", example = "1")
    @Column(nullable = false)
    @Comment("所属分类ID（必填）")
    private Long categoryId;

    @Schema(description = "子分类ID", example = "2")
    @Column
    @Comment("子分类ID（可选）")
    private Long subCategoryId;

    @Schema(description = "供应商ID", example = "1")
    @Column(nullable = false)
    @Comment("供应商ID（必填）")
    private Long supplierId;

    @Schema(description = "计量单位", example = "斤")
    @Column(length = 20)
    @Comment("计量单位（如：斤、袋、箱）")
    private String productUnit;

    @Schema(description = "规格描述", example = "500g/袋")
    @Column(length = 100)
    @Comment("规格描述（如：500g/袋）")
    private String productSpec;

    @Schema(description = "销售价", example = "29.90")
    @Column(nullable = false, precision = 10, scale = 2)
    @Comment("销售价（必填，精确到小数点后2位）")
    private BigDecimal productPrice;

    @Schema(description = "成本价", example = "20.00")
    @Column(precision = 10, scale = 2)
    @Comment("成本价（可选）")
    private BigDecimal costPrice;

    @Schema(description = "当前库存", example = "100")
    @Column(nullable = false)
    @Comment("当前库存（默认0）")
    private Integer stockQuantity = 0;

    @Schema(description = "最小起订量", example = "1")
    @Column(nullable = false)
    @Comment("最小起订量（默认1）")
    private Integer minOrderQuantity = 1;

    @Schema(description = "是否热销商品", example = "false")
    @Column(nullable = false)
    @Comment("是否热销商品（默认false）")
    private Boolean isHotProduct = false;

    @Schema(description = "是否新品", example = "true")
    @Column(nullable = false)
    @Comment("是否新品（默认false）")
    private Boolean isNewProduct = false;

    @Schema(description = "商品状态", example = "1")
    @Column(nullable = false)
    @Comment("状态（1-上架，0-下架，默认1）")
    private Integer productStatus = 1;

    @Schema(description = "主图URL")
    @Column(length = 500)
    @Comment("主图URL（可选）")
    private String mainImageUrl;

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
