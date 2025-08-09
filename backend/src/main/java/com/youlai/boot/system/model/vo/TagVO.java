package com.youlai.boot.system.model.vo;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 标签视图对象
 *
 * @author youlaitech
 * @since 2025-01-01
 */
@Getter
@Setter
@Schema(description = "标签视图对象")
public class TagVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "标签ID")
    private Long id;
    
    @Schema(description = "标签名称")
    private String name;
    
    @Schema(description = "标签颜色")
    private String color;
}