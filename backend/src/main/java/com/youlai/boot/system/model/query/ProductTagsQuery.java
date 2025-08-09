package com.youlai.boot.system.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 标签分页查询对象
 *
 * @author liuzijun
 * @since 2025-08-08 12:10
 */
@Schema(description ="标签查询对象")
@Getter
@Setter
public class ProductTagsQuery extends BasePageQuery {

    @Schema(description = "标签名称")
    private String name;

    @Schema(description = "父级标签ID")
    private Integer parentId;

}
