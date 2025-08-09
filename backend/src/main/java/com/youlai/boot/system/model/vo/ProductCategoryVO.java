package com.youlai.boot.system.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
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

    private Long id;
    @Schema(description = "分类名称")
    private String name;
    @Schema(description = "分类图释")
    private String icon;
    @Schema(description = "一级id")
    private Long parentId;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "子分类列表")
    private List<ProductCategoryVO> children;
}
