package com.youlai.boot.system.model.entity;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;

/**
 * 商品表实体对象
 *
 * @author youlaitech
 * @since 2025-08-08 11:28
 */
@Getter
@Setter
@TableName("product")
public class Product extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品图片，JSON数组格式存储多张图片URL
     */
    private String images;
    /**
     * 商品简介
     */
    private String description;
    /**
     * 原价
     */
    private BigDecimal price;
    /**
     * 是否有优惠
     */
    private Integer hasDiscount;
    /**
     * 优惠价
     */
    private BigDecimal discountPrice;
    /**
     * 销量
     */
    private Integer sales;

    /**
     * 产地
     */
    private String origin;
    /**
     * 是否为热门推荐商品
     */
    private Integer isHot;

    /**
     * 虚拟销量

     */
    private Integer virtualSales;
    /**
     * 库存
     */
    private Long stock;
    /**
     * 是否上架

     */
    private Integer isOnline;
    /**
     * 规格
     */
    private String type;
    /**
     * 视频
     */
    private String vedio;
}
