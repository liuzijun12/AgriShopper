package com.agrishopper.repository;

import com.agrishopper.model.WxUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 微信用户数据访问层
 */
@Repository
public interface WxUserRepository extends JpaRepository<WxUser, Long> {
    
    /**
     * 根据openid查找用户
     * @param openid 微信openid
     * @return 用户信息
     */
    Optional<WxUser> findByOpenid(String openid);
    
    /**
     * 根据unionid查找用户
     * @param unionid 微信unionid
     * @return 用户信息
     */
    Optional<WxUser> findByUnionid(String unionid);
    
    /**
     * 检查openid是否存在
     * @param openid 微信openid
     * @return 是否存在
     */
    boolean existsByOpenid(String openid);
} 