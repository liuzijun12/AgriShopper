package com.youlai.boot.system.converter;

import org.mapstruct.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.IdTags;
import com.youlai.boot.system.model.form.IdTagsForm;

/**
 * 标签关联表对象转换器
 *
 * @author liuzijun
 * @since 2025-08-08 12:20
 */
@Mapper(componentModel = "spring")
public interface IdTagsConverter{

    IdTagsForm toForm(IdTags entity);

    IdTags toEntity(IdTagsForm formData);
}