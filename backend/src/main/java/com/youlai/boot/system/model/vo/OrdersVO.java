package com.youlai.boot.system.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 订单视图对象
 *
 * @author youlaitech
 * @since 2025-08-16 00:58
 */
@Getter
@Setter
@Schema( description = "订单视图对象")
public class OrdersVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    @Schema(description = "关联用户ID")
    private Integer userId;
    @Schema(description = "完整地址快照")
    private String addressSnapshot;
    @Schema(description = "关联原始地址")
    private Integer originalAddressId;
    @Schema(description = "订单的状态")
    private String status;
    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;
    @Schema(description = "是否软删除")
    private Integer isDeleted;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "删除时间")
    private LocalDateTime deleteTime;
}
