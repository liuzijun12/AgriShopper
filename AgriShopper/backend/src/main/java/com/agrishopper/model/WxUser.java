package com.agrishopper.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 微信用户实体类
 */
@Entity
@Table(name = "wxuser")
@Schema(description = "微信用户信息")
public class WxUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "用户ID")
    @Comment("用户ID")
    private Long id;
    
    @Column(unique = true, nullable = false)
    @Schema(description = "微信openid")
    @Comment("微信openid")
    private String openid;
    
    @Column
    @Schema(description = "微信unionid")
    @Comment("微信unionid")
    private String unionid;
    
    @Column
    @Schema(description = "用户昵称")
    @Comment("用户昵称")
    private String nickname;
    
    @Column
    @Schema(description = "用户头像")
    @Comment("用户头像")
    private String avatar;
    
    @Column
    @Schema(description = "性别：0-未知，1-男，2-女")
    @Comment("性别：0-未知，1-男，2-女")
    private Integer gender;
    
    @Column
    @Schema(description = "手机号")
    @Comment("手机号")
    private String phone;
    
    @Column
    @Schema(description = "国家")
    @Comment("国家")
    private String country;
    
    @Column
    @Schema(description = "省份")
    @Comment("省份")
    private String province;
    
    @Column
    @Schema(description = "城市")
    @Comment("城市")
    private String city;
    
    @Column(name = "create_time", nullable = false)
    @Schema(description = "创建时间")
    @Comment("创建时间")
    private LocalDateTime createTime;
    
    @Column(name = "update_time", nullable = false)
    @Schema(description = "更新时间")
    @Comment("更新时间")
    private LocalDateTime updateTime;
    
    // 构造函数
    public WxUser() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOpenid() {
        return openid;
    }
    
    public void setOpenid(String openid) {
        this.openid = openid;
    }
    
    public String getUnionid() {
        return unionid;
    }
    
    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public Integer getGender() {
        return gender;
    }
    
    public void setGender(Integer gender) {
        this.gender = gender;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getProvince() {
        return province;
    }
    
    public void setProvince(String province) {
        this.province = province;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 