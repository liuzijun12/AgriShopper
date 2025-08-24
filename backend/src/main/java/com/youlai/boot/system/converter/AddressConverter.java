package com.youlai.boot.system.converter;

import org.mapstruct.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.Address;
import com.youlai.boot.system.model.form.AddressForm;

/**
 * 存储用户收货地址信息对象转换器
 *
 * @author youlaitech
 * @since 2025-08-16 00:52
 */
@Mapper(componentModel = "spring")
public interface AddressConverter{

    AddressForm toForm(Address entity);

    Address toEntity(AddressForm formData);
}