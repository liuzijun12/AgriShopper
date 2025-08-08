package com.youlai.boot.system.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 分类视图对象
 *
 * @author liuzijun
 * @since 2025-08-08 12:02
 */
@Getter
@Setter
@Schema( description = "分类视图对象")
public class ProductCategoryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    @Schema(description = "分类名称")
    private String name;
    @Schema(description = "分类图释")
    private String icon;
    @Schema(description = "一级id")
    private Integer parentId;
}
