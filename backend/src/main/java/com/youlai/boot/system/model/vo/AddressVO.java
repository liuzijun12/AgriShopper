package com.youlai.boot.system.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 存储用户收货地址信息视图对象
 *
 * @author youlaitech
 * @since 2025-08-16 00:52
 */
@Getter
@Setter
@Schema( description = "存储用户收货地址信息视图对象")
public class AddressVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    @Schema(description = "关联用户ID")
    private Integer userId;
    @Schema(description = "收货人姓名")
    private String receiverName;
    @Schema(description = "联系电话")
    private String phone;
    @Schema(description = "省份")
    private String province;
    @Schema(description = "城市")
    private String city;
    @Schema(description = "区县")
    private String district;
    @Schema(description = "详细地址")
    private String detailAddress;
    @Schema(description = "邮政编码")
    private String postalCode;
    @Schema(description = "是否默认地址")
    private Integer isDefault;
    @Schema(description = "是否软删除")
    private Integer isDeleted;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "删除时间")
    private LocalDateTime deleteTime;
}
