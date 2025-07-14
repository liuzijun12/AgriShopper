package com.agrishopper.service;

import com.agrishopper.model.WxUser;
import java.util.Optional;

/**
 * 微信用户服务接口
 */
public interface WxUserService {
    
    /**
     * 微信用户登录/注册
     * @param code 微信登录code
     * @param userInfo 用户信息（可选）
     * @return 用户信息
     */
    WxUser wxLogin(String code, WxUser userInfo);
    
    /**
     * 根据openid查找用户
     * @param openid 微信openid
     * @return 用户信息
     */
    Optional<WxUser> findByOpenid(String openid);
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户信息
     */
    Optional<WxUser> findById(Long id);
    
    /**
     * 保存或更新用户信息
     * @param wxUser 用户信息
     * @return 保存后的用户信息
     */
    WxUser save(WxUser wxUser);
    
    /**
     * 更新用户信息
     * @param id 用户ID
     * @param wxUser 更新的用户信息
     * @return 更新后的用户信息
     */
    WxUser updateUser(Long id, WxUser wxUser);
    
    /**
     * 检查用户是否存在
     * @param openid 微信openid
     * @return 是否存在
     */
    boolean existsByOpenid(String openid);
} 