package com.youlai.boot.system.model.entity;

import lombok.Getter;
import lombok.Setter;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;

/**
 * 分类实体对象
 *
 * @author liuzijun
 * @since 2025-08-08 12:02
 */
@Getter
@Setter
@TableName("product_category")
public class ProductCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
    private String name;
    /**
     * 分类图释
     */
    private String icon;
    /**
     * 一级id
     */
    private Integer parentId;
}
