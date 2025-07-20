package com.agrishopper.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户地址实体类
 */
@Entity
@Table(name = "userAddress")
public class UserAddress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "userId", nullable = false)
    private Long userId;
    
    @Column(name = "receiverName", nullable = false, length = 50)
    private String receiverName;
    
    @Column(name = "receiverPhone", nullable = false, length = 20)
    private String receiverPhone;
    
    @Column(name = "province", nullable = false, length = 50)
    private String province;
    
    @Column(name = "city", nullable = false, length = 50)
    private String city;
    
    @Column(name = "district", nullable = false, length = 50)
    private String district;
    
    @Column(name = "detailAddress", nullable = false, length = 200)
    private String detailAddress;
    
    @Column(name = "postalCode", length = 10)
    private String postalCode;
    
    @Column(name = "isDefault", nullable = false)
    private Boolean isDefault = false;
    
    @Column(name = "isDeleted", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "createTime", nullable = false)
    private LocalDateTime createTime;
    
    @Column(name = "updateTime", nullable = false)
    private LocalDateTime updateTime;
    
    // 构造函数
    public UserAddress() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getReceiverName() {
        return receiverName;
    }
    
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
    
    public String getReceiverPhone() {
        return receiverPhone;
    }
    
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
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
    
    public String getDistrict() {
        return district;
    }
    
    public void setDistrict(String district) {
        this.district = district;
    }
    
    public String getDetailAddress() {
        return detailAddress;
    }
    
    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
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
    
    // 获取完整地址的方法
    public String getFullAddress() {
        return province + city + district + detailAddress;
    }
    
    // 获取简短地址的方法（不包含详细地址）
    public String getShortAddress() {
        return province + city + district;
    }
    
    @Override
    public String toString() {
        return "UserAddress{" +
                "id=" + id +
                ", userId=" + userId +
                ", receiverName='" + receiverName + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", detailAddress='" + detailAddress + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", isDefault=" + isDefault +
                ", isDeleted=" + isDeleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 