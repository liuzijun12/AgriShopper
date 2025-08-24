package com.youlai.boot.system.converter;

import org.mapstruct.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.Orders;
import com.youlai.boot.system.model.form.OrdersForm;

/**
 * 订单对象转换器
 *
 * @author youlaitech
 * @since 2025-08-16 00:58
 */
@Mapper(componentModel = "spring")
public interface OrdersConverter{

    OrdersForm toForm(Orders entity);

    Orders toEntity(OrdersForm formData);
}