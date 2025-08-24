package com.youlai.boot.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.mapper.AddressMapper;
import com.youlai.boot.system.service.AddressService;
import com.youlai.boot.system.model.entity.Address;
import com.youlai.boot.system.model.form.AddressForm;
import com.youlai.boot.system.model.query.AddressQuery;
import com.youlai.boot.system.model.vo.AddressVO;
import com.youlai.boot.system.converter.AddressConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * 存储用户收货地址信息服务实现类
 *
 * @author youlaitech
 * @since 2025-08-16 00:52
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    private final AddressConverter addressConverter;

    /**
    * 获取存储用户收货地址信息分页列表
    *
    * @param queryParams 查询参数
    * @return {@link IPage<AddressVO>} 存储用户收货地址信息分页列表
    */
    @Override
    public IPage<AddressVO> getAddressPage(AddressQuery queryParams) {
        Page<AddressVO> pageVO = this.baseMapper.getAddressPage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return pageVO;
    }
    
    /**
     * 获取存储用户收货地址信息表单数据
     *
     * @param id 存储用户收货地址信息ID
     * @return 存储用户收货地址信息表单数据
     */
    @Override
    public AddressForm getAddressFormData(Long id) {
        Address entity = this.getById(id);
        return addressConverter.toForm(entity);
    }
    
    /**
     * 新增存储用户收货地址信息
     *
     * @param formData 存储用户收货地址信息表单对象
     * @return 是否新增成功
     */
    @Override
    public boolean saveAddress(AddressForm formData) {
        Address entity = addressConverter.toEntity(formData);
        return this.save(entity);
    }
    
    /**
     * 更新存储用户收货地址信息
     *
     * @param id   存储用户收货地址信息ID
     * @param formData 存储用户收货地址信息表单对象
     * @return 是否修改成功
     */
    @Override
    public boolean updateAddress(Long id,AddressForm formData) {
        Address entity = addressConverter.toEntity(formData);
        return this.updateById(entity);
    }
    
    /**
     * 删除存储用户收货地址信息
     *
     * @param ids 存储用户收货地址信息ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    @Override
    public boolean deleteAddresss(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的存储用户收货地址信息数据为空");
        // 逻辑删除
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return this.removeByIds(idList);
    }

}
