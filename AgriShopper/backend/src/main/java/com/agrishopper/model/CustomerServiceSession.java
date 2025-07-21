package com.agrishopper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customerservicesession")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CustomerServiceSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "sessionCode", unique = true, nullable = false, length = 50)
    private String sessionCode;
    
    @Column(name = "userId", nullable = false)
    private Long userId;
    
    @Column(name = "productId")
    private Long productId;
    
    @Column(name = "orderId", length = 50)
    private String orderId;
    
    @Column(name = "sessionType", nullable = false)
    private Integer sessionType; // 1-产品咨询，2-订单咨询，3-售后服务，4-其他
    
    @Column(name = "sessionStatus", nullable = false)
    private Integer sessionStatus; // 1-进行中，2-已结束，3-已关闭
    
    @Column(name = "priority", nullable = false)
    private Integer priority; // 1-高，2-中，3-低
    
    @Column(name = "assignedAgentId")
    private Long assignedAgentId;
    
    @Column(name = "autoReplyEnabled", nullable = false)
    private Boolean autoReplyEnabled = true;
    
    @Column(name = "aiEnabled", nullable = false)
    private Boolean aiEnabled = false; // 暂时关闭AI功能
    
    @Column(name = "aiModel", length = 50)
    private String aiModel;
    
    @Column(name = "lastMessageTime")
    private LocalDateTime lastMessageTime;
    
    @Column(name = "createTime", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "updateTime", nullable = false)
    private LocalDateTime updateTime;
    
    @Column(name = "isDeleted", nullable = false)
    private Boolean isDeleted = false; // 软删除标记
    
    // 关联实体 - 使用JsonIgnore避免序列化问题
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private WxUser user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignedAgentId", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Admin assignedAgent;
    
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