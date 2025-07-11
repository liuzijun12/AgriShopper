package com.agrishopper.repository;

import com.agrishopper.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByUserId(Long userId);
    
    Address findByUserIdAndIsDefaultTrue(Long userId);
    
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId AND a.id != :addressId")
    void unsetOtherDefaultAddresses(Long userId, Long addressId);
} 