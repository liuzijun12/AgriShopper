package com.youlai.boot.system.converter;

import org.mapstruct.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.IdCategory;
import com.youlai.boot.system.model.form.IdCategoryForm;

/**
 * 分类关联对象转换器
 *
 * @author liuzijun
 * @since 2025-08-08 12:16
 */
@Mapper(componentModel = "spring")
public interface IdCategoryConverter{

    IdCategoryForm toForm(IdCategory entity);

    IdCategory toEntity(IdCategoryForm formData);
}