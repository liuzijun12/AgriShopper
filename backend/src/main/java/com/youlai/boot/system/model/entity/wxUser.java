package com.youlai.boot.system.model.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;

/**
 * 用户实体对象
 *
 * @author liuzijun
 * @since 2025-08-13 15:03
 */
@Getter
@Setter
@TableName("user")
public class wxUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 微信用户唯一ID
     */
    private String openid;
    /**
     * 微信开放平台ID
     */
    private String unionid;
    /**
     * 微信昵称
     */
    private String nickname;
    /**
     * 头像URL
     */
    private String avatar;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 地区
     */
    private String district;
    /**
     * 是否是管理员
     */
    private Integer isManager;
    /**
     * 是否是超级管理员
     */
    private Integer isSupermanager;
    /**
     * 账户余额
     */
    private BigDecimal balance;
    /**
     * 是否软删除
     */
    private Integer isDeleted;
    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;
}
