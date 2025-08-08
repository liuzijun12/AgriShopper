package com.youlai.boot.system.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 分类分页查询对象
 *
 * @author liuzijun
 * @since 2025-08-08 12:02
 */
@Schema(description ="分类查询对象")
@Getter
@Setter
public class ProductCategoryQuery extends BasePageQuery {

}
