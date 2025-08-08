package com.youlai.boot.system.converter;

import org.mapstruct.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.ProductCategory;
import com.youlai.boot.system.model.form.ProductCategoryForm;

/**
 * 分类对象转换器
 *
 * @author liuzijun
 * @since 2025-08-08 12:02
 */
@Mapper(componentModel = "spring")
public interface ProductCategoryConverter{

    ProductCategoryForm toForm(ProductCategory entity);

    ProductCategory toEntity(ProductCategoryForm formData);
}