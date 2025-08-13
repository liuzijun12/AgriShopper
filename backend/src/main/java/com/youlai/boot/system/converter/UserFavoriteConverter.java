package com.youlai.boot.system.converter;

import org.mapstruct.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.UserFavorite;
import com.youlai.boot.system.model.form.UserFavoriteForm;

/**
 * 收藏对象转换器
 *
 * @author liuzijun
 * @since 2025-08-13 20:03
 */
@Mapper(componentModel = "spring")
public interface UserFavoriteConverter{

    UserFavoriteForm toForm(UserFavorite entity);

    UserFavorite toEntity(UserFavoriteForm formData);
}