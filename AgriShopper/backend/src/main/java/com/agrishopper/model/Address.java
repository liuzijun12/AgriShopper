package com.agrishopper.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Schema(description = "收货地址实体类")
@Data
@Entity
@Table(name = "address")
public class Address {
    
    @Schema(description = "地址ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("地址ID，自增主键")
    private Long id;

    @Schema(description = "关联的用户")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("关联的用户ID")
    @JsonIgnoreProperties({"addresses", "password"})
    private User user;

    @Schema(description = "收货人姓名", example = "张三")
    @Column(nullable = false, length = 50)
    @Comment("收货人姓名")
    private String receiverName;

    @Schema(description = "收货人手机号", example = "13800138000")
    @Column(nullable = false, length = 15)
    @Comment("收货人手机号")
    private String receiverPhone;

    @Schema(description = "省份", example = "广东省")
    @Column(nullable = false, length = 50)
    @Comment("省份")
    private String province;

    @Schema(description = "城市", example = "深圳市")
    @Column(nullable = false, length = 50)
    @Comment("城市")
    private String city;

    @Schema(description = "区/县", example = "南山区")
    @Column(nullable = false, length = 50)
    @Comment("区/县")
    private String district;

    @Schema(description = "详细地址", example = "科技园路1号")
    @Column(nullable = false, length = 255)
    @Comment("详细地址")
    private String detailAddress;

    @Schema(description = "是否为默认地址", example = "false")
    @Column(name = "is_default")
    @Comment("是否为默认地址")
    private Boolean isDefault = false;

    @Schema(description = "创建时间")
    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @Column(name = "update_time")
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
} 