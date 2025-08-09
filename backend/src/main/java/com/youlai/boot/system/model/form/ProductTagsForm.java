package com.youlai.boot.system.model.form;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

/**
 * 标签表单对象
 *
 * @author liuzijun
 * @since 2025-08-08 12:10
 */
@Getter
@Setter
@Schema(description = "标签表单对象")
public class ProductTagsForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "不能为空")
    private Integer id;

    @Schema(description = "标签名")
    @Size(max=255, message="标签名长度不能超过255个字符")
    private String name;

    private Integer parentId;


}
