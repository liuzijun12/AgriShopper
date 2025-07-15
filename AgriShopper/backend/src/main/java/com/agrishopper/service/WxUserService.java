package com.agrishopper.service;

import com.agrishopper.model.WxUser;
import java.util.Optional;
import java.util.Map;

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
     * 微信用户登录/注册（带解密）
     * @param code 微信登录code
     * @param userInfo 用户信息（可选）
     * @param encryptedData 加密的用户数据
     * @param iv 加密算法的初始向量
     * @param signature 数据签名
     * @return 用户信息
     */
    WxUser wxLogin(String code, WxUser userInfo, String encryptedData, String iv, String signature);
    
    /**
     * 微信用户登录/注册（带session解密）
     * @param code 微信登录code
     * @param userInfo 用户信息（可选）
     * @param encryptedData 加密的用户数据
     * @param iv 加密算法的初始向量
     * @param signature 数据签名
     * @param sessionInfo session信息
     * @return 用户信息
     */
    WxUser wxLogin(String code, WxUser userInfo, String encryptedData, String iv, String signature, Map<String, Object> sessionInfo);
    
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
    
    /**
     * 通过code获取session信息
     * @param code 微信登录code
     * @return session信息（包含openid、session_key、unionid）
     */
    Map<String, String> getSessionInfo(String code);
} 