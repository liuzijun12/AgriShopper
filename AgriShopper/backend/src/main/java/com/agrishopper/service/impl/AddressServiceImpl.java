package com.agrishopper.service.impl;

import com.agrishopper.model.Address;
import com.agrishopper.model.User;
import com.agrishopper.repository.AddressRepository;
import com.agrishopper.repository.UserRepository;
import com.agrishopper.service.AddressService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Address createAddress(Long userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
        
        address.setUser(user);
        
        // 如果是第一个地址，设置为默认地址
        if (addressRepository.findByUserId(userId).isEmpty()) {
            address.setIsDefault(true);
        }
        
        // 如果设置为默认地址，取消其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.unsetOtherDefaultAddresses(userId, 0L);
        }
        
        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public Address updateAddress(Long addressId, Address address) {
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("地址不存在"));
        
        existingAddress.setReceiverName(address.getReceiverName());
        existingAddress.setReceiverPhone(address.getReceiverPhone());
        existingAddress.setProvince(address.getProvince());
        existingAddress.setCity(address.getCity());
        existingAddress.setDistrict(address.getDistrict());
        existingAddress.setDetailAddress(address.getDetailAddress());
        
        // 如果设置为默认地址，取消其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault()) && !Boolean.TRUE.equals(existingAddress.getIsDefault())) {
            addressRepository.unsetOtherDefaultAddresses(existingAddress.getUser().getId(), addressId);
            existingAddress.setIsDefault(true);
        }
        
        return addressRepository.save(existingAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("地址不存在"));
        
        addressRepository.delete(address);
        
        // 如果删除的是默认地址，且还有其他地址，则将第一个地址设为默认
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            List<Address> remainingAddresses = addressRepository.findByUserId(address.getUser().getId());
            if (!remainingAddresses.isEmpty()) {
                Address firstAddress = remainingAddresses.get(0);
                firstAddress.setIsDefault(true);
                addressRepository.save(firstAddress);
            }
        }
    }

    @Override
    public Optional<Address> getAddressById(Long addressId) {
        return addressRepository.findById(addressId);
    }

    @Override
    @Transactional
    public List<Address> getAddressesByUserId(Long userId) {
        // 先检查用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
        return addressRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Address setDefaultAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("地址不存在"));
        
        if (!address.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("该地址不属于当前用户");
        }
        
        addressRepository.unsetOtherDefaultAddresses(userId, addressId);
        address.setIsDefault(true);
        return addressRepository.save(address);
    }

    @Override
    public Optional<Address> getDefaultAddress(Long userId) {
        return Optional.ofNullable(addressRepository.findByUserIdAndIsDefaultTrue(userId));
    }
} 