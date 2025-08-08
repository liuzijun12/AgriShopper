package com.youlai.boot.system.model.entity;

import lombok.Getter;
import lombok.Setter;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;

/**
 * 标签关联表实体对象
 *
 * @author liuzijun
 * @since 2025-08-08 12:20
 */
@Getter
@Setter
@TableName("id_tags")
public class IdTags extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Integer productId;
    private Integer tagsId;
}
