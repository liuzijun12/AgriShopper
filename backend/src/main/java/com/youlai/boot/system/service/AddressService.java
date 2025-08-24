package com.youlai.boot.system.service;

import com.youlai.boot.system.model.entity.Address;
import com.youlai.boot.system.model.form.AddressForm;
import com.youlai.boot.system.model.query.AddressQuery;
import com.youlai.boot.system.model.vo.AddressVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 存储用户收货地址信息服务类
 *
 * @author youlaitech
 * @since 2025-08-16 00:52
 */
public interface AddressService extends IService<Address> {

    /**
     *存储用户收货地址信息分页列表
     *
     * @return {@link IPage<AddressVO>} 存储用户收货地址信息分页列表
     */
    IPage<AddressVO> getAddressPage(AddressQuery queryParams);

    /**
     * 获取存储用户收货地址信息表单数据
     *
     * @param id 存储用户收货地址信息ID
     * @return 存储用户收货地址信息表单数据
     */
     AddressForm getAddressFormData(Long id);

    /**
     * 新增存储用户收货地址信息
     *
     * @param formData 存储用户收货地址信息表单对象
     * @return 是否新增成功
     */
    boolean saveAddress(AddressForm formData);

    /**
     * 修改存储用户收货地址信息
     *
     * @param id   存储用户收货地址信息ID
     * @param formData 存储用户收货地址信息表单对象
     * @return 是否修改成功
     */
    boolean updateAddress(Long id, AddressForm formData);

    /**
     * 删除存储用户收货地址信息
     *
     * @param ids 存储用户收货地址信息ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    boolean deleteAddresss(String ids);

}
