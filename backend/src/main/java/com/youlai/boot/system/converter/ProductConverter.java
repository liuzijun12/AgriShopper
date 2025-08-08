package com.youlai.boot.system.converter;

import org.mapstruct.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.Product;
import com.youlai.boot.system.model.form.ProductForm;

/**
 * 商品表对象转换器
 *
 * @author youlaitech
 * @since 2025-08-08 11:28
 */
@Mapper(componentModel = "spring")
public interface ProductConverter{

    ProductForm toForm(Product entity);

    Product toEntity(ProductForm formData);
}