package com.youlai.boot.core.security.extension.wx;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.system.model.entity.WxUser;
import com.youlai.boot.system.service.WxUserService;
import com.youlai.boot.core.security.model.WxUserDetails;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * 微信小程序Code认证Provider
 *
 * @author 有来技术团队
 * @since 2.0.0
 */
@Slf4j
public class WxMiniAppCodeAuthenticationProvider implements AuthenticationProvider {

    private final WxUserService wxUserService;
    private final WxMaService wxMaService;


    public WxMiniAppCodeAuthenticationProvider(WxUserService wxUserService, WxMaService wxMaService) {
        this.wxUserService = wxUserService;
        this.wxMaService = wxMaService;
    }


    /**
     * 微信认证逻辑，参考 Spring Security 认证密码校验流程
     *
     * @param authentication 认证对象
     * @return 认证后的 Authentication 对象
     * @throws AuthenticationException 认证异常
     * @see org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider#authenticate(Authentication)
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WxMiniAppCodeAuthenticationToken authenticationToken = (WxMiniAppCodeAuthenticationToken) authentication;
        String code = (String) authenticationToken.getPrincipal();
        Object userInfo = authenticationToken.getUserInfo(); // 获取用户信息

        // 通过微信服务端验证 code 并获取用户会话信息
        WxMaJscode2SessionResult sessionInfo;
        try {
            sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            throw new CredentialsExpiredException("微信登录 code 无效或已失效，请重新获取");
        }

        String openId = sessionInfo.getOpenid();
        String unionId = sessionInfo.getUnionid();
        if (StrUtil.isBlank(openId)) {
            throw new UsernameNotFoundException("未能获取到微信 OpenID，请稍后重试");
        }

        // 根据微信 OpenID 查询用户信息
        WxUser wxUser = wxUserService.getByOpenId(openId);

        if (wxUser == null) {
            // 用户不存在则注册，传递用户信息
            boolean registered = wxUserService.registerOrUpdateWxUser(openId, unionId, userInfo);
            if (!registered) {
                throw new UsernameNotFoundException("用户注册失败，请稍后重试");
            }
            
            // 再次查询用户信息，确保用户注册成功
            wxUser = wxUserService.getByOpenId(openId);
            if (wxUser == null) {
                throw new UsernameNotFoundException("用户注册失败，请稍后重试");
            }
        } else if (userInfo != null) {
            // 用户存在且有用户信息，更新用户信息
            wxUserService.updateWxUserInfo(openId, userInfo);
            // 重新查询用户信息
            wxUser = wxUserService.getByOpenId(openId);
        }

        // 检查用户状态是否有效
        if (ObjectUtil.notEqual(wxUser.getIsDeleted(), 0)) {
            throw new DisabledException("用户已被删除");
        }

        // 构建认证后的用户详情信息
        WxUserDetails userDetails = new WxUserDetails(wxUser);

        // 创建已认证的Token
        return WxMiniAppCodeAuthenticationToken.authenticated(
                userDetails,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WxMiniAppCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
} 