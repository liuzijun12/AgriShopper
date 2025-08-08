package com.youlai.boot.system.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 标签关联表视图对象
 *
 * @author liuzijun
 * @since 2025-08-08 12:20
 */
@Getter
@Setter
@Schema( description = "标签关联表视图对象")
public class IdTagsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer productId;
    private Integer tagsId;
}
