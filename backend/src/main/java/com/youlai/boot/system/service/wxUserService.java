package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.WxUser;

/**
 * 微信用户服务接口
 */
public interface WxUserService extends IService<WxUser> {

    /**
     * 根据OpenID获取微信用户
     *
     * @param openId 微信OpenID
     * @return 微信用户信息
     */
    WxUser getByOpenId(String openId);

    /**
     * 注册或更新微信用户
     *
     * @param openId 微信OpenID
     * @param unionId 微信UnionID
     * @param userInfo 用户信息
     * @return 是否成功
     */
    boolean registerOrUpdateWxUser(String openId, String unionId, Object userInfo);

    /**
     * 更新微信用户信息
     *
     * @param openId 微信OpenID
     * @param userInfo 用户信息
     * @return 是否成功
     */
    boolean updateWxUserInfo(String openId, Object userInfo);
} 