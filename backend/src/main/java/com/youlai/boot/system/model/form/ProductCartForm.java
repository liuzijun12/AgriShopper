package com.youlai.boot.system.model.form;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

/**
 * 购物车表单对象
 *
 * @author liuzijun
 * @since 2025-08-13 21:13
 */
@Getter
@Setter
@Schema(description = "购物车表单对象")
public class ProductCartForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    @Schema(description = "用户唯一标识")
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @Schema(description = "商品ID")
    @NotNull(message = "商品ID不能为空")
    private Integer productId;

    @Schema(description = "选择商品的规格")
    @Size(max=255, message="商品规格长度不能超过255个字符")
    private String productType;

    @Schema(description = "商品的数量")
    @NotNull(message = "商品数量不能为空")
    private Integer productCount;

    @Schema(description = "是否软删除")
    private Integer isDeleted;

    @Schema(description = "所选规格的价格")
    @NotNull(message = "商品价格不能为空")
    private BigDecimal productPrice;

    @Schema(description = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "删除时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteTime;


}
