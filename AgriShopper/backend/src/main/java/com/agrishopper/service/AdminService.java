package com.agrishopper.service;

import com.agrishopper.dto.LoginRequest;
import com.agrishopper.dto.LoginResponse;
import com.agrishopper.model.Admin;
import java.util.List;
import java.util.Optional;

public interface AdminService {
    
    /**
     * 管理员登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * 获取所有管理员
     * @return 管理员列表
     */
    List<Admin> getAllAdmins();
    
    /**
     * 根据ID获取管理员
     * @param id 管理员ID
     * @return 管理员
     */
    Optional<Admin> getAdminById(Long id);
    
    /**
     * 根据手机号获取管理员
     * @param phone 手机号
     * @return 管理员
     */
    Optional<Admin> getAdminByPhone(String phone);
    
    /**
     * 根据昵称获取管理员
     * @param nickname 昵称
     * @return 管理员
     */
    Optional<Admin> getAdminByNickname(String nickname);
    
    /**
     * 保存管理员
     * @param admin 管理员
     * @return 保存后的管理员
     */
    Admin saveAdmin(Admin admin);
    
    /**
     * 删除管理员
     * @param id 管理员ID
     */
    void deleteAdmin(Long id);
    
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