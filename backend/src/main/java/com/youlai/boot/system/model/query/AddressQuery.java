package com.youlai.boot.system.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 存储用户收货地址信息分页查询对象
 *
 * @author youlaitech
 * @since 2025-08-16 00:52
 */
@Schema(description ="存储用户收货地址信息查询对象")
@Getter
@Setter
public class AddressQuery extends BasePageQuery {

}
