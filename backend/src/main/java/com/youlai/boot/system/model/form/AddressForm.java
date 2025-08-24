package com.youlai.boot.system.model.form;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

/**
 * 存储用户收货地址信息表单对象
 *
 * @author youlaitech
 * @since 2025-08-16 00:52
 */
@Getter
@Setter
@Schema(description = "存储用户收货地址信息表单对象")
public class AddressForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    @Schema(description = "关联用户ID")
    @NotNull(message = "关联用户ID不能为空")
    private Integer userId;

    @Schema(description = "收货人姓名")
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max=50, message="收货人姓名长度不能超过50个字符")
    private String receiverName;

    @Schema(description = "联系电话")
    @NotBlank(message = "联系电话不能为空")
    @Size(max=20, message="联系电话长度不能超过20个字符")
    private String phone;

    @Schema(description = "省份")
    @NotBlank(message = "省份不能为空")
    @Size(max=20, message="省份长度不能超过20个字符")
    private String province;

    @Schema(description = "城市")
    @NotBlank(message = "城市不能为空")
    @Size(max=20, message="城市长度不能超过20个字符")
    private String city;

    @Schema(description = "区县")
    @NotBlank(message = "区县不能为空")
    @Size(max=20, message="区县长度不能超过20个字符")
    private String district;

    @Schema(description = "详细地址")
    @NotBlank(message = "详细地址不能为空")
    @Size(max=255, message="详细地址长度不能超过255个字符")
    private String detailAddress;

    @Schema(description = "邮政编码")
    @Size(max=10, message="邮政编码长度不能超过10个字符")
    private String postalCode;

    @Schema(description = "是否默认地址")
    @NotNull(message = "是否默认地址不能为空")
    private Integer isDefault;

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
