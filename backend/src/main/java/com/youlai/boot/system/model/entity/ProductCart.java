package com.youlai.boot.system.model.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;

/**
 * 购物车实体对象
 *
 * @author liuzijun
 * @since 2025-08-13 21:13
 */
@Getter
@Setter
@TableName("product_cart")
public class ProductCart extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     */
    private Integer userId;
    /**
     * 商品ID
     */
    private Integer productId;
    /**
     * 选择商品的规格
     */
    private String productType;
    /**
     * 商品的数量
     */
    private Integer productCount;
    /**
     * 是否软删除
     */
    private Integer isDeleted;
    /**
     * 所选规格的价格
     */
    private BigDecimal productPrice;
    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;
}
