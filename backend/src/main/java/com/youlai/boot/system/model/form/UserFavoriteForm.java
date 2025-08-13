package com.youlai.boot.system.model.form;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

/**
 * 收藏表单对象
 *
 * @author liuzijun
 * @since 2025-08-13 20:03
 */
@Getter
@Setter
@Schema(description = "收藏表单对象")
public class UserFavoriteForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    @Schema(description = "识别用户的唯一标识")
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @Schema(description = "商品的id")
    @NotNull(message = "商品ID不能为空")
    private Integer productId;

    @Schema(description = "是否软删除")
    private Integer isDeleted;

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
