package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.Address;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.query.AddressQuery;
import com.youlai.boot.system.model.vo.AddressVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 存储用户收货地址信息Mapper接口
 *
 * @author youlaitech
 * @since 2025-08-16 00:52
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {

    /**
     * 获取存储用户收货地址信息分页数据
     *
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return {@link Page<AddressVO>} 存储用户收货地址信息分页列表
     */
    Page<AddressVO> getAddressPage(Page<AddressVO> page, AddressQuery queryParams);

}
