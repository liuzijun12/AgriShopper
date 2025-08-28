package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 微信用户实体
 */
@TableName("user")
@Getter
@Setter
public class WxUser extends BaseEntity {

    /**
     * 微信 OpenID
     */
    @TableField("openid")
    private String openid;

    /**
     * 微信开放平台ID
     */
    @TableField("unionid")
    private String unionid;

    /**
     * 微信昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 性别((1-男 2-女 0-保密)
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 省份
     */
    @TableField("province")
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 地区
     */
    @TableField("district")
    private String district;

    /**
     * 是否是管理员
     */
    @TableField("is_manager")
    private Integer isManager;

    /**
     * 是否是超级管理员
     */
    @TableField("is_supermanager")
    private Integer isSupermanager;

    /**
     * 账户余额
     */
    @TableField("balance")
    private BigDecimal balance;

    /**
     * 是否软删除
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 删除时间
     */
    @TableField("delete_time")
    private LocalDateTime deleteTime;

} 