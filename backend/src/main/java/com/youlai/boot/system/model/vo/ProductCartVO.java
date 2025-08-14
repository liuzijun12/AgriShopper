package com.youlai.boot.system.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 购物车视图对象
 *
 * @author liuzijun
 * @since 2025-08-13 21:13
 */
@Getter
@Setter
@Schema( description = "购物车视图对象")
public class ProductCartVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    @Schema(description = "用户唯一标识")
    private Integer userId;
    @Schema(description = "商品ID")
    private Integer productId;
    @Schema(description = "选择商品的规格")
    private Object productType;
    @Schema(description = "商品的数量")
    private Integer productCount;
    @Schema(description = "是否软删除")
    private Integer isDeleted;
    @Schema(description = "所选规格的价格")
    private BigDecimal productPrice;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "删除时间")
    private LocalDateTime deleteTime;
    @Schema(description = "用户姓名")
    private String userName;
    @Schema(description = "商品名称")
    private String productName;
    @Schema(description = "商品图片")
    private String productImages;
}
