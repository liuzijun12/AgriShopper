package com.youlai.boot.system.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * 商品表视图对象
 *
 * @author youlaitech
 * @since 2025-08-08 11:28
 */
@Getter
@Setter
@Schema( description = "商品表视图对象")
public class ProductVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品ID")
    private Long id;
    @Schema(description = "商品名称")
    private String name;
    @Schema(description = "商品图片，JSON数组格式存储多张图片URL")
    private String images;
    @Schema(description = "商品简介")
    private String description;
    @Schema(description = "原价")
    private BigDecimal price;
    @Schema(description = "是否有优惠")
    private Integer hasDiscount;
    @Schema(description = "优惠价")
    private BigDecimal discountPrice;
    @Schema(description = "销量")
    private Integer sales;
    @Schema(description = "标签名称，多个标签用逗号分隔")
    private String tagNames;
    @Schema(description = "分类名称，多个分类用逗号分隔")
    private String categoryNames;
    @Schema(description = "产地")
    private String origin;
    @Schema(description = "是否为热门推荐商品")
    private Integer isHot;
    @Schema(description = "虚拟销量")
    private Integer virtualSales;
    @Schema(description = "库存")
    private Long stock;
    @Schema(description = "是否上架")
    private Integer isOnline;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
