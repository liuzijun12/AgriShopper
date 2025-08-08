package com.youlai.boot.system.model.entity;

import lombok.Getter;
import lombok.Setter;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;

/**
 * 标签实体对象
 *
 * @author liuzijun
 * @since 2025-08-08 12:10
 */
@Getter
@Setter
@TableName("product_tags")
public class ProductTags extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名

     */
    private String name;
    private Integer parentId;
}
