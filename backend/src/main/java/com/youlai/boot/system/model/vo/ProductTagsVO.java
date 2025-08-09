package com.youlai.boot.system.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 标签视图对象
 *
 * @author liuzijun
 * @since 2025-08-08 12:10
 */
@Getter
@Setter
@Schema( description = "标签视图对象")
public class ProductTagsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "标签ID")
    private Integer id;
    
    @Schema(description = "标签名")
    private String name;
    
    @Schema(description = "父级标签ID")
    private Integer parentId;
    
    @Schema(description = "子标签列表")
    private List<ProductTagsVO> children;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
