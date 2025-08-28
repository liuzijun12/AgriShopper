package com.youlai.boot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.mapper.WxUserMapper;
import com.youlai.boot.system.model.entity.WxUser;
import com.youlai.boot.system.service.WxUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 微信用户服务实现类
 */
@Slf4j
@Service
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements WxUserService {

    @Override
    public WxUser getByOpenId(String openId) {
        if (openId == null || openId.trim().isEmpty()) {
            return null;
        }
        return this.getOne(
                new LambdaQueryWrapper<WxUser>()
                        .eq(WxUser::getOpenid, openId)
                        .eq(WxUser::getIsDeleted, 0)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerOrUpdateWxUser(String openId, String unionId, Object userInfo) {
        if (openId == null || openId.trim().isEmpty()) {
            return false;
        }

        // 查询是否已存在该openId的用户
        WxUser existUser = this.getByOpenId(openId);

        if (existUser != null) {
            // 用户已存在，更新信息
            existUser.setUnionid(unionId);
            existUser.setUpdateTime(LocalDateTime.now());
            return this.updateById(existUser);
        } else {
            // 创建新用户
            WxUser newUser = new WxUser();
            newUser.setOpenid(openId);
            newUser.setUnionid(unionId);
            newUser.setNickname("微信用户");
            newUser.setGender(0); // 保密
            newUser.setIsManager(0); // 普通用户
            newUser.setIsSupermanager(0); // 非超级管理员
            newUser.setBalance(new java.math.BigDecimal("0.00"));
            newUser.setIsDeleted(0);
            newUser.setCreateTime(LocalDateTime.now());
            newUser.setUpdateTime(LocalDateTime.now());
            return this.save(newUser);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWxUserInfo(String openId, Object userInfo) {
        if (openId == null || openId.trim().isEmpty()) {
            return false;
        }

        // 查询用户是否存在
        WxUser existUser = this.getByOpenId(openId);
        if (existUser == null) {
            log.warn("用户不存在，无法更新用户信息: {}", openId);
            return false;
        }

        // 更新用户信息
        updateUserInfoFromWxUserInfo(existUser, userInfo);
        existUser.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(existUser);
    }

    /**
     * 从微信用户信息更新用户信息
     */
    private void updateUserInfoFromWxUserInfo(WxUser wxUser, Object userInfo) {
        try {
            // 使用反射获取用户信息字段
            if (userInfo instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> infoMap = (java.util.Map<String, Object>) userInfo;
                
                // 更新昵称
                if (infoMap.containsKey("nickName")) {
                    wxUser.setNickname((String) infoMap.get("nickName"));
                }
                
                // 更新头像
                if (infoMap.containsKey("avatarUrl")) {
                    wxUser.setAvatar((String) infoMap.get("avatarUrl"));
                }
                
                // 更新性别
                if (infoMap.containsKey("gender")) {
                    wxUser.setGender((Integer) infoMap.get("gender"));
                }
                
                // 更新地区信息
                if (infoMap.containsKey("province")) {
                    wxUser.setProvince((String) infoMap.get("province"));
                }
                
                if (infoMap.containsKey("city")) {
                    wxUser.setCity((String) infoMap.get("city"));
                }
            }
        } catch (Exception e) {
            log.warn("更新微信用户信息失败: {}", e.getMessage());
        }
    }
} 