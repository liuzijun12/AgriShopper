package com.agrishopper.repository;

import com.agrishopper.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户地址仓库接口
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    
    /**
     * 根据用户ID查询所有未删除的地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    @Query("SELECT ua FROM UserAddress ua WHERE ua.userId = :userId AND ua.isDeleted = false ORDER BY ua.isDefault DESC, ua.createTime DESC")
    List<UserAddress> findByUserIdAndNotDeleted(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    @Query("SELECT ua FROM UserAddress ua WHERE ua.userId = :userId AND ua.isDefault = true AND ua.isDeleted = false")
    Optional<UserAddress> findDefaultAddressByUserId(@Param("userId") Long userId);
    
    /**
     * 根据地址ID和用户ID查询地址（确保只能操作自己的地址）
     * @param id 地址ID
     * @param userId 用户ID
     * @return 地址信息
     */
    @Query("SELECT ua FROM UserAddress ua WHERE ua.id = :id AND ua.userId = :userId AND ua.isDeleted = false")
    Optional<UserAddress> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * 统计用户未删除的地址数量
     * @param userId 用户ID
     * @return 地址数量
     */
    @Query("SELECT COUNT(ua) FROM UserAddress ua WHERE ua.userId = :userId AND ua.isDeleted = false")
    long countByUserIdAndNotDeleted(@Param("userId") Long userId);
    
    /**
     * 软删除地址（将isDeleted设置为true）
     * @param id 地址ID
     * @param userId 用户ID
     * @return 影响的行数
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAddress ua SET ua.isDeleted = true, ua.updateTime = CURRENT_TIMESTAMP WHERE ua.id = :id AND ua.userId = :userId")
    int softDeleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * 取消用户的所有默认地址
     * @param userId 用户ID
     * @return 影响的行数
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAddress ua SET ua.isDefault = false, ua.updateTime = CURRENT_TIMESTAMP WHERE ua.userId = :userId AND ua.isDefault = true AND ua.isDeleted = false")
    int cancelAllDefaultAddresses(@Param("userId") Long userId);
    
    /**
     * 设置指定地址为默认地址
     * @param id 地址ID
     * @param userId 用户ID
     * @return 影响的行数
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAddress ua SET ua.isDefault = true, ua.updateTime = CURRENT_TIMESTAMP WHERE ua.id = :id AND ua.userId = :userId AND ua.isDeleted = false")
    int setDefaultAddress(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * 检查用户是否已有默认地址
     * @param userId 用户ID
     * @return 是否有默认地址
     */
    @Query("SELECT COUNT(ua) > 0 FROM UserAddress ua WHERE ua.userId = :userId AND ua.isDefault = true AND ua.isDeleted = false")
    boolean existsDefaultAddressByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和地址ID检查地址是否存在且未删除
     * @param id 地址ID
     * @param userId 用户ID
     * @return 是否存在
     */
    @Query("SELECT COUNT(ua) > 0 FROM UserAddress ua WHERE ua.id = :id AND ua.userId = :userId AND ua.isDeleted = false")
    boolean existsByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * 获取用户的所有地址（包括已删除的，用于管理员查看）
     * @param userId 用户ID
     * @return 所有地址列表
     */
    @Query("SELECT ua FROM UserAddress ua WHERE ua.userId = :userId ORDER BY ua.isDefault DESC, ua.createTime DESC")
    List<UserAddress> findAllByUserId(@Param("userId") Long userId);
} 