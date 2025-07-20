package com.agrishopper.service;

import com.agrishopper.model.UserAddress;
import java.util.List;

/**
 * 用户地址服务接口
 */
public interface UserAddressService {
    
    /**
     * 获取用户的所有地址列表（包括默认地址标识）
     * 用于在地址管理页面展示用户的所有地址
     * @param userId 用户ID
     * @return 地址列表，默认地址排在前面
     */
    List<UserAddress> getUserAddresses(Long userId);
    
    /**
     * 获取用户的默认地址
     * 用于在结算页面或需要默认地址的场景
     * @param userId 用户ID
     * @return 默认地址，如果没有则返回null
     */
    UserAddress getDefaultAddress(Long userId);
    
    /**
     * 新增地址
     * 用户添加新的收货地址
     * @param userAddress 地址信息
     * @return 新增的地址信息
     */
    UserAddress addAddress(UserAddress userAddress);
    
    /**
     * 修改地址
     * 用户修改已有的收货地址信息
     * @param userAddress 修改后的地址信息
     * @return 修改后的地址信息
     */
    UserAddress updateAddress(UserAddress userAddress);
    
    /**
     * 删除地址
     * 用户删除不需要的收货地址（软删除）
     * @param addressId 地址ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteAddress(Long addressId, Long userId);
    
    /**
     * 设置默认地址
     * 用户将某个地址设置为默认收货地址
     * @param addressId 地址ID
     * @param userId 用户ID
     * @return 是否设置成功
     */
    boolean setDefaultAddress(Long addressId, Long userId);
    
    /**
     * 根据ID获取地址详情
     * 用于编辑地址时获取地址信息
     * @param addressId 地址ID
     * @param userId 用户ID
     * @return 地址详情
     */
    UserAddress getAddressById(Long addressId, Long userId);
    
    /**
     * 检查用户地址数量限制
     * 防止用户添加过多地址
     * @param userId 用户ID
     * @return 当前地址数量
     */
    long getAddressCount(Long userId);
    
    /**
     * 验证地址信息
     * 检查地址信息的完整性和有效性
     * @param userAddress 地址信息
     * @return 验证结果
     */
    boolean validateAddress(UserAddress userAddress);
} 