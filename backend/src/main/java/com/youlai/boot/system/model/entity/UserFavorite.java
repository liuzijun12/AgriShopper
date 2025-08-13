package com.youlai.boot.system.model.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;

/**
 * 收藏实体对象
 *
 * @author liuzijun
 * @since 2025-08-13 20:03
 */
@Getter
@Setter
@TableName("user_favorite")
public class UserFavorite extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 识别用户的唯一标识
     */
    private Integer userId;
    /**
     * 商品的id
     */
    private Integer productId;
    /**
     * 是否软删除
     */
    private Integer isDeleted;
    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;
}
