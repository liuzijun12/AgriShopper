package com.agrishopper.service.impl;

import com.agrishopper.model.UserAddress;
import com.agrishopper.repository.UserAddressRepository;
import com.agrishopper.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户地址服务实现类
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {
    
    @Autowired
    private UserAddressRepository userAddressRepository;
    
    // 最大地址数量限制
    private static final int MAX_ADDRESS_COUNT = 20;
    
    @Override
    public List<UserAddress> getUserAddresses(Long userId) {
        // 获取用户所有未删除的地址，默认地址会自动排在前面
        return userAddressRepository.findByUserIdAndNotDeleted(userId);
    }
    
    @Override
    public UserAddress getDefaultAddress(Long userId) {
        // 获取用户的默认地址
        Optional<UserAddress> defaultAddress = userAddressRepository.findDefaultAddressByUserId(userId);
        return defaultAddress.orElse(null);
    }
    
    @Override
    @Transactional
    public UserAddress addAddress(UserAddress userAddress) {
        // 验证地址信息
        if (!validateAddress(userAddress)) {
            throw new IllegalArgumentException("地址信息不完整或格式不正确");
        }
        
        // 检查地址数量限制
        long currentCount = userAddressRepository.countByUserIdAndNotDeleted(userAddress.getUserId());
        if (currentCount >= MAX_ADDRESS_COUNT) {
            throw new IllegalArgumentException("地址数量已达上限，最多只能添加" + MAX_ADDRESS_COUNT + "个地址");
        }
        
        // 设置创建时间和更新时间
        userAddress.setCreateTime(LocalDateTime.now());
        userAddress.setUpdateTime(LocalDateTime.now());
        userAddress.setIsDeleted(false);
        
        // 如果设置为默认地址，需要先取消其他默认地址
        if (userAddress.getIsDefault()) {
            userAddressRepository.cancelAllDefaultAddresses(userAddress.getUserId());
        } else {
            // 如果这是第一个地址，自动设为默认地址
            if (currentCount == 0) {
                userAddress.setIsDefault(true);
            }
        }
        
        // 保存地址
        return userAddressRepository.save(userAddress);
    }
    
    @Override
    @Transactional
    public UserAddress updateAddress(UserAddress userAddress) {
        // 验证地址信息
        if (!validateAddress(userAddress)) {
            throw new IllegalArgumentException("地址信息不完整或格式不正确");
        }
        
        // 检查地址是否存在且属于该用户
        Optional<UserAddress> existingAddress = userAddressRepository.findByIdAndUserId(
            userAddress.getId(), userAddress.getUserId());
        
        if (!existingAddress.isPresent()) {
            throw new IllegalArgumentException("地址不存在或无权限修改");
        }
        
        UserAddress existing = existingAddress.get();
        
        // 更新地址信息
        existing.setReceiverName(userAddress.getReceiverName());
        existing.setReceiverPhone(userAddress.getReceiverPhone());
        existing.setProvince(userAddress.getProvince());
        existing.setCity(userAddress.getCity());
        existing.setDistrict(userAddress.getDistrict());
        existing.setDetailAddress(userAddress.getDetailAddress());
        existing.setPostalCode(userAddress.getPostalCode());
        existing.setUpdateTime(LocalDateTime.now());
        
        // 处理默认地址设置
        if (userAddress.getIsDefault() && !existing.getIsDefault()) {
            // 如果要设为默认地址，先取消其他默认地址
            userAddressRepository.cancelAllDefaultAddresses(userAddress.getUserId());
            existing.setIsDefault(true);
        }
        
        // 保存更新
        return userAddressRepository.save(existing);
    }
    
    @Override
    @Transactional
    public boolean deleteAddress(Long addressId, Long userId) {
        // 检查地址是否存在且属于该用户
        if (!userAddressRepository.existsByIdAndUserId(addressId, userId)) {
            return false;
        }
        
        // 软删除地址
        int affectedRows = userAddressRepository.softDeleteByIdAndUserId(addressId, userId);
        
        // 如果删除的是默认地址，需要设置其他地址为默认地址
        if (affectedRows > 0) {
            Optional<UserAddress> deletedAddress = userAddressRepository.findByIdAndUserId(addressId, userId);
            if (deletedAddress.isPresent() && deletedAddress.get().getIsDefault()) {
                // 查找其他地址设为默认地址
                List<UserAddress> remainingAddresses = userAddressRepository.findByUserIdAndNotDeleted(userId);
                if (!remainingAddresses.isEmpty()) {
                    UserAddress newDefault = remainingAddresses.get(0);
                    userAddressRepository.setDefaultAddress(newDefault.getId(), userId);
                }
            }
        }
        
        return affectedRows > 0;
    }
    
    @Override
    @Transactional
    public boolean setDefaultAddress(Long addressId, Long userId) {
        // 检查地址是否存在且属于该用户
        if (!userAddressRepository.existsByIdAndUserId(addressId, userId)) {
            return false;
        }
        
        // 取消所有默认地址
        userAddressRepository.cancelAllDefaultAddresses(userId);
        
        // 设置新的默认地址
        int affectedRows = userAddressRepository.setDefaultAddress(addressId, userId);
        
        return affectedRows > 0;
    }
    
    @Override
    public UserAddress getAddressById(Long addressId, Long userId) {
        // 根据ID和用户ID获取地址详情
        Optional<UserAddress> address = userAddressRepository.findByIdAndUserId(addressId, userId);
        return address.orElse(null);
    }
    
    @Override
    public long getAddressCount(Long userId) {
        // 获取用户当前地址数量
        return userAddressRepository.countByUserIdAndNotDeleted(userId);
    }
    
    @Override
    public boolean validateAddress(UserAddress userAddress) {
        // 验证必填字段
        if (userAddress == null) {
            return false;
        }
        
        if (!StringUtils.hasText(userAddress.getReceiverName())) {
            return false;
        }
        
        if (!StringUtils.hasText(userAddress.getReceiverPhone())) {
            return false;
        }
        
        if (!StringUtils.hasText(userAddress.getProvince())) {
            return false;
        }
        
        if (!StringUtils.hasText(userAddress.getCity())) {
            return false;
        }
        
        if (!StringUtils.hasText(userAddress.getDistrict())) {
            return false;
        }
        
        if (!StringUtils.hasText(userAddress.getDetailAddress())) {
            return false;
        }
        
        // 验证手机号格式（简单验证）
        String phone = userAddress.getReceiverPhone();
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return false;
        }
        
        // 验证邮政编码格式（如果提供）
        if (StringUtils.hasText(userAddress.getPostalCode())) {
            String postalCode = userAddress.getPostalCode();
            if (!postalCode.matches("^\\d{6}$")) {
                return false;
            }
        }
        
        return true;
    }
} 