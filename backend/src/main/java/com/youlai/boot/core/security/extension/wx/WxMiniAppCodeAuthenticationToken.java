package com.youlai.boot.core.security.extension.wx;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Collection;

/**
 * 微信小程序Code认证Token
 *
 * @author 有来技术团队
 * @since 2.0.0
 */
public class WxMiniAppCodeAuthenticationToken extends AbstractAuthenticationToken {
    @Serial
    private static final long serialVersionUID = 621L;
    private final Object principal;
    private Object userInfo; // 添加用户信息字段

    /**
     * 微信小程序Code认证Token (未认证)
     *
     * @param principal 微信code
     * @param userInfo 用户信息（可选）
     */
    public WxMiniAppCodeAuthenticationToken(Object principal, Object userInfo) {
        // 没有授权信息时，设置为 null
        super(null);
        this.principal = principal;
        this.userInfo = userInfo;
        // 默认未认证
        this.setAuthenticated(false);
    }

    /**
     * 微信小程序Code认证Token (未认证) - 兼容旧版本
     *
     * @param principal 微信code
     */
    public WxMiniAppCodeAuthenticationToken(Object principal) {
        this(principal, null);
    }

    /**
     * 微信小程序Code认证Token (已认证)
     *
     * @param principal   微信用户信息
     * @param authorities 授权信息
     */
    public WxMiniAppCodeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.userInfo = null; // 已认证时不需要用户信息
        // 认证通过
        super.setAuthenticated(true);
    }

    /**
     * 认证通过
     *
     * @param principal   微信用户信息
     * @param authorities 授权信息
     * @return 已认证的Token
     */
    public static WxMiniAppCodeAuthenticationToken authenticated(Object principal, Collection<? extends GrantedAuthority> authorities) {
        return new WxMiniAppCodeAuthenticationToken(principal, authorities);
    }

    @Override
    public Object getCredentials() {
        // 微信认证不需要密码
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    public Object getUserInfo() {
        return userInfo;
    }
} 