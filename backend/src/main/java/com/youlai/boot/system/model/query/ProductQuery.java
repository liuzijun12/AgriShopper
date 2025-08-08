package com.youlai.boot.system.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * 商品表分页查询对象
 *
 * @author youlaitech
 * @since 2025-08-08 11:28
 */
@Schema(description ="商品表查询对象")
@Getter
@Setter
public class ProductQuery extends BasePageQuery {

}
