package com.youlai.boot.system.model.form;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.*;

/**
 * 商品表表单对象
 *
 * @author youlaitech
 * @since 2025-08-08 11:28
 */
@Getter
@Setter
@Schema(description = "商品表表单对象")
public class ProductForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "商品名称")
    @NotBlank(message = "商品名称不能为空")
    @Size(max=100, message="商品名称长度不能超过100个字符")
    private String name;

    @Schema(description = "商品图片，JSON数组格式存储多张图片URL")
    @NotBlank(message = "商品图片，JSON数组格式存储多张图片URL不能为空")
    @Size(max=65535, message="商品图片，JSON数组格式存储多张图片URL长度不能超过65535个字符")
    private String images;

    @Schema(description = "商品简介")
    @NotBlank(message = "商品简介不能为空")
    @Size(max=65535, message="商品简介长度不能超过65535个字符")
    private String description;

    @Schema(description = "原价")
    @NotNull(message = "原价不能为空")
    private BigDecimal price;

    @Schema(description = "是否有优惠")
    private Integer hasDiscount;

    @Schema(description = "优惠价")
    private BigDecimal discountPrice;

    @Schema(description = "销量")
    private Integer sales;



    @Schema(description = "产地")
    @Size(max=100, message="产地长度不能超过100个字符")
    private String origin;

    @Schema(description = "是否为热门推荐商品")
    private Integer isHot;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "虚拟销量")
    private Integer virtualSales;

    @Schema(description = "库存")
    private Long stock;

    @Schema(description = "是否上架")
    private Integer isOnline;

    @Schema(description = "规格")
    private String type;

    @Schema(description = "视频")
    private String vedio;

    @Schema(description = "分类ID列表")
    private List<Long> categoryIds;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;

}
