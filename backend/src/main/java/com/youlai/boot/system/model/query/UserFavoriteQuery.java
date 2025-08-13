package com.youlai.boot.system.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 收藏分页查询对象
 *
 * @author liuzijun
 * @since 2025-08-13 20:03
 */
@Schema(description ="收藏查询对象")
@Getter
@Setter
public class UserFavoriteQuery extends BasePageQuery {

    @Schema(description = "用户ID")
    private Integer userId;
    
    @Schema(description = "商品ID")
    private Integer productId;
    
    @Schema(description = "用户名称")
    private String userName;
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "创建时间范围")
    private List<String> createTime;

}
