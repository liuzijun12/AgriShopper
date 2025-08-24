package com.youlai.boot.system.model.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;

/**
 * 订单实体对象
 *
 * @author youlaitech
 * @since 2025-08-16 00:58
 */
@Getter
@Setter
@TableName("orders")
public class Orders extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联用户ID
     */
    private Integer userId;
    /**
     * 完整地址快照
     */
    private String addressSnapshot;
    /**
     * 关联原始地址
     */
    private Integer originalAddressId;
    /**
     * 订单的状态
     */
    private String status;
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    /**
     * 是否软删除
     */
    private Integer isDeleted;
    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;
}
