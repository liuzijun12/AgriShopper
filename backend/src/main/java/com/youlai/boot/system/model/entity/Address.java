package com.youlai.boot.system.model.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;

/**
 * 存储用户收货地址信息实体对象
 *
 * @author youlaitech
 * @since 2025-08-16 00:52
 */
@Getter
@Setter
@TableName("address")
public class Address extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联用户ID
     */
    private Integer userId;
    /**
     * 收货人姓名
     */
    private String receiverName;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 区县
     */
    private String district;
    /**
     * 详细地址
     */
    private String detailAddress;
    /**
     * 邮政编码
     */
    private String postalCode;
    /**
     * 是否默认地址
     */
    private Integer isDefault;
    /**
     * 是否软删除
     */
    private Integer isDeleted;
    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;
}
