package com.agrishopper.repository;

import com.agrishopper.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    /**
     * 根据手机号查找管理员
     * @param phone 手机号
     * @return 管理员
     */
    Optional<Admin> findByPhone(String phone);
    
    /**
     * 根据昵称查找管理员
     * @param nickname 昵称
     * @return 管理员
     */
    Optional<Admin> findByNickname(String nickname);
    
    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 检查昵称是否存在
     * @param nickname 昵称
     * @return 是否存在
     */
    boolean existsByNickname(String nickname);
} 