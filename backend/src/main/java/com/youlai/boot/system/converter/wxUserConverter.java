package com.youlai.boot.system.converter;

import org.mapstruct.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.wxUser;
import com.youlai.boot.system.model.form.wxUserForm;

/**
 * 用户对象转换器
 *
 * @author liuzijun
 * @since 2025-08-13 15:03
 */
@Mapper(componentModel = "spring")
public interface wxUserConverter{

    wxUserForm toForm(wxUser entity);

    wxUser toEntity(wxUserForm formData);
}