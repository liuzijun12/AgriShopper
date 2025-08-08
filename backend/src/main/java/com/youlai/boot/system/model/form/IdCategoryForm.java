package com.youlai.boot.system.model.form;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

/**
 * 分类关联表单对象
 *
 * @author liuzijun
 * @since 2025-08-08 12:16
 */
@Getter
@Setter
@Schema(description = "分类关联表单对象")
public class IdCategoryForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "不能为空")
    private Integer id;

    private Integer productId;

    private Integer categoryId;


}
