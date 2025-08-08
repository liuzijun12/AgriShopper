package com.youlai.boot.system.model.form;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

/**
 * 标签关联表表单对象
 *
 * @author liuzijun
 * @since 2025-08-08 12:20
 */
@Getter
@Setter
@Schema(description = "标签关联表表单对象")
public class IdTagsForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "不能为空")
    private Integer id;

    private Integer productId;

    private Integer tagsId;


}
