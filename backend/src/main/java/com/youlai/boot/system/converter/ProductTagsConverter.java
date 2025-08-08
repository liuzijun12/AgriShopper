package com.youlai.boot.system.converter;

import org.mapstruct.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.ProductTags;
import com.youlai.boot.system.model.form.ProductTagsForm;

/**
 * 标签对象转换器
 *
 * @author liuzijun
 * @since 2025-08-08 12:10
 */
@Mapper(componentModel = "spring")
public interface ProductTagsConverter{

    ProductTagsForm toForm(ProductTags entity);

    ProductTags toEntity(ProductTagsForm formData);
}