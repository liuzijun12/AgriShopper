package com.youlai.boot.system.model.form;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

/**
 * 分类表单对象
 *
 * @author liuzijun
 * @since 2025-08-08 12:02
 */
@Getter
@Setter
@Schema(description = "分类表单对象")
public class ProductCategoryForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "不能为空")
    private Integer id;

    @Schema(description = "分类名称")
    @Size(max=255, message="分类名称长度不能超过255个字符")
    private String name;

    @Schema(description = "分类图释")
    @Size(max=255, message="分类图释长度不能超过255个字符")
    private String icon;

    @Schema(description = "一级id")
    private Integer parentId;


}
