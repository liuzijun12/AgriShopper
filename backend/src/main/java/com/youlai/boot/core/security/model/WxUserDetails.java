package com.youlai.boot.core.security.model;

import com.youlai.boot.system.model.entity.WxUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 微信用户详情
 */
@Getter
public class WxUserDetails implements UserDetails {

    private final WxUser wxUser;

    public WxUserDetails(WxUser wxUser) {
        this.wxUser = wxUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 微信用户默认拥有USER角色
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // 微信用户不需要密码
        return null;
    }

    @Override
    public String getUsername() {
        return wxUser.getOpenid();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return wxUser.getIsDeleted() == 0;
    }

    public String getOpenId() {
        return wxUser.getOpenid();
    }

    public String getUnionId() {
        return wxUser.getUnionid();
    }

    public String getNickname() {
        return wxUser.getNickname();
    }

    public String getAvatar() {
        return wxUser.getAvatar();
    }
} 