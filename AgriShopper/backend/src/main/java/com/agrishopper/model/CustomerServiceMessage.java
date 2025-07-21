package com.agrishopper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "customerservicemessage")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CustomerServiceMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "sessionId", nullable = false)
    private Long sessionId;
    
    @Column(name = "senderType", nullable = false)
    private Integer senderType; // 1-用户，2-客服，3-系统，4-AI助手
    
    @Column(name = "senderId")
    private Long senderId;
    
    @Column(name = "messageType", nullable = false)
    private Integer messageType; // 1-文本，2-图片，3-语音，4-文件，5-系统通知
    
    @Column(name = "messageContent", nullable = false, columnDefinition = "TEXT")
    private String messageContent;
    
    @Column(name = "messageMediaUrl", length = 500)
    private String messageMediaUrl;
    
    @Column(name = "messageDuration")
    private Integer messageDuration; // 语音消息时长（秒）
    
    @Column(name = "isRead", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "readTime")
    private LocalDateTime readTime;
    
    @Column(name = "aiGenerated", nullable = false)
    private Boolean aiGenerated = false; // 是否为AI生成的消息
    
    @Column(name = "aiConfidence", precision = 3, scale = 2)
    private BigDecimal aiConfidence; // AI回复的置信度（0-1）
    
    @Column(name = "aiModel", length = 50)
    private String aiModel; // 生成此消息的AI模型
    
    @Column(name = "createTime", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "isDeleted", nullable = false)
    private Boolean isDeleted = false; // 软删除标记
    
    // 关联实体 - 使用JsonIgnore避免序列化问题
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessionId", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CustomerServiceSession session;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
} 