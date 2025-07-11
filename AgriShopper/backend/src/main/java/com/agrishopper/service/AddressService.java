package com.agrishopper.service;

import com.agrishopper.model.Address;
import java.util.List;
import java.util.Optional;

public interface AddressService {
    
    Address createAddress(Long userId, Address address);
    
    Address updateAddress(Long addressId, Address address);
    
    void deleteAddress(Long addressId);
    
    Optional<Address> getAddressById(Long addressId);
    
    List<Address> getAddressesByUserId(Long userId);
    
    Address setDefaultAddress(Long userId, Long addressId);
    
    Optional<Address> getDefaultAddress(Long userId);
} 