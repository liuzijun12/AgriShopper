package com.youlai.boot.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.mapper.OrdersMapper;
import com.youlai.boot.system.service.OrdersService;
import com.youlai.boot.system.model.entity.Orders;
import com.youlai.boot.system.model.form.OrdersForm;
import com.youlai.boot.system.model.query.OrdersQuery;
import com.youlai.boot.system.model.vo.OrdersVO;
import com.youlai.boot.system.converter.OrdersConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * 订单服务实现类
 *
 * @author youlaitech
 * @since 2025-08-16 00:58
 */
@Service
@RequiredArgsConstructor
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    private final OrdersConverter ordersConverter;

    /**
    * 获取订单分页列表
    *
    * @param queryParams 查询参数
    * @return {@link IPage<OrdersVO>} 订单分页列表
    */
    @Override
    public IPage<OrdersVO> getOrdersPage(OrdersQuery queryParams) {
        Page<OrdersVO> pageVO = this.baseMapper.getOrdersPage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return pageVO;
    }
    
    /**
     * 获取订单表单数据
     *
     * @param id 订单ID
     * @return 订单表单数据
     */
    @Override
    public OrdersForm getOrdersFormData(Long id) {
        Orders entity = this.getById(id);
        return ordersConverter.toForm(entity);
    }
    
    /**
     * 新增订单
     *
     * @param formData 订单表单对象
     * @return 是否新增成功
     */
    @Override
    public boolean saveOrders(OrdersForm formData) {
        Orders entity = ordersConverter.toEntity(formData);
        return this.save(entity);
    }
    
    /**
     * 更新订单
     *
     * @param id   订单ID
     * @param formData 订单表单对象
     * @return 是否修改成功
     */
    @Override
    public boolean updateOrders(Long id,OrdersForm formData) {
        Orders entity = ordersConverter.toEntity(formData);
        return this.updateById(entity);
    }
    
    /**
     * 删除订单
     *
     * @param ids 订单ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    @Override
    public boolean deleteOrderss(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的订单数据为空");
        // 逻辑删除
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return this.removeByIds(idList);
    }

}
