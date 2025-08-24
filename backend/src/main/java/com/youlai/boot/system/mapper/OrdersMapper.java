package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.Orders;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.query.OrdersQuery;
import com.youlai.boot.system.model.vo.OrdersVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper接口
 *
 * @author youlaitech
 * @since 2025-08-16 00:58
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

    /**
     * 获取订单分页数据
     *
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return {@link Page<OrdersVO>} 订单分页列表
     */
    Page<OrdersVO> getOrdersPage(Page<OrdersVO> page, OrdersQuery queryParams);

}
