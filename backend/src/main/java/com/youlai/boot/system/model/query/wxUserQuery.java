package com.youlai.boot.system.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

/**
 * 用户分页查询对象
 *
 * @author liuzijun
 * @since 2025-08-13 15:03
 */
@Schema(description ="用户查询对象")
@Getter
@Setter
public class wxUserQuery extends BasePageQuery {

}
