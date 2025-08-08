package com.youlai.boot.system.model.entity;

import lombok.Getter;
import lombok.Setter;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;

/**
 * 分类关联实体对象
 *
 * @author liuzijun
 * @since 2025-08-08 12:16
 */
@Getter
@Setter
@TableName("id_category")
public class IdCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Integer productId;
    private Integer categoryId;
}
