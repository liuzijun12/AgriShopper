package com.youlai.boot.system.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 收藏视图对象
 *
 * @author liuzijun
 * @since 2025-08-13 20:03
 */
@Getter
@Setter
@Schema( description = "收藏视图对象")
public class UserFavoriteVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    @Schema(description = "识别用户的唯一标识")
    private Integer userId;
    @Schema(description = "商品的id")
    private Integer productId;
    @Schema(description = "是否软删除")
    private Integer isDeleted;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "删除时间")
    private LocalDateTime deleteTime;
    
    // 关联信息
    @Schema(description = "用户名称")
    private String userName;
    @Schema(description = "商品名称")
    private String productName;
    @Schema(description = "商品图片")
    private String productImages;
    @Schema(description = "商品价格")
    private java.math.BigDecimal productPrice;
}
