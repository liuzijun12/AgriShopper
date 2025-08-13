package com.youlai.boot.system.converter;

import org.mapstruct.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.ProductCart;
import com.youlai.boot.system.model.form.ProductCartForm;

/**
 * 购物车对象转换器
 *
 * @author liuzijun
 * @since 2025-08-13 21:13
 */
@Mapper(componentModel = "spring")
public interface ProductCartConverter{

    ProductCartForm toForm(ProductCart entity);

    ProductCart toEntity(ProductCartForm formData);
}