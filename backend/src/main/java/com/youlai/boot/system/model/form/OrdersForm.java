package com.youlai.boot.system.model.form;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

/**
 * 订单表单对象
 *
 * @author youlaitech
 * @since 2025-08-16 00:58
 */
@Getter
@Setter
@Schema(description = "订单表单对象")
public class OrdersForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    @Schema(description = "关联用户ID")
    @NotNull(message = "关联用户ID不能为空")
    private Integer userId;

    @Schema(description = "完整地址快照")
    @NotBlank(message = "完整地址快照不能为空")
    private String addressSnapshot;

    @Schema(description = "关联原始地址")
    @NotNull(message = "关联原始地址不能为空")
    private Integer originalAddressId;

    @Schema(description = "订单的状态")
    @NotBlank(message = "订单的状态不能为空")
    @Size(max=20, message="订单的状态长度不能超过20个字符")
    private String status;

    @Schema(description = "订单总金额")
    @NotNull(message = "订单总金额不能为空")
    private BigDecimal totalAmount;

    @Schema(description = "是否软删除")
    private Integer isDeleted;

    @Schema(description = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "删除时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteTime;


}
