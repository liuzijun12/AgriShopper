package com.agrishopper.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.Comment;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户行为记录实体类
 * 用于存储用户的各种操作行为，如查看商品、点击商品、加入购物车等
 */
@Schema(description = "用户行为记录实体类")
@Data
@Entity
@Table(name = "userBehavior")
public class UserBehavior {

    @Schema(description = "行为记录ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("行为记录ID，自增主键")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    @Column(name = "userId", nullable = false)
    @Comment("用户ID（关联wxuser表）")
    private Long userId;

    @Schema(description = "行为类型", example = "VIEW_PRODUCT")
    @Column(name = "behaviorType", nullable = false, length = 50)
    @Comment("行为类型（VIEW_PRODUCT-查看商品，CLICK_PRODUCT-点击商品，ADD_TO_CART-加入购物车，ADD_TO_FAVORITE-收藏商品，REMOVE_FROM_FAVORITE-取消收藏，SEARCH_PRODUCT-搜索商品，VIEW_CATEGORY-查看分类，VIEW_PAGE-浏览页面）")
    private String behaviorType;

    @Schema(description = "目标类型", example = "PRODUCT")
    @Column(name = "targetType", nullable = false, length = 50)
    @Comment("目标类型（PRODUCT-商品，CATEGORY-分类，PAGE-页面，SEARCH-搜索）")
    private String targetType;

    @Schema(description = "目标ID", example = "P001")
    @Column(name = "targetId", length = 100)
    @Comment("目标ID（商品编码、分类ID、页面路径等）")
    private String targetId;

    @Schema(description = "目标名称", example = "有机大米")
    @Column(name = "targetName", length = 500)
    @Comment("目标名称（商品名称、分类名称、页面标题等）")
    private String targetName;

    @Schema(description = "目标URL", example = "/pages/productDetail/productDetail?productCode=P001")
    @Column(name = "targetUrl", length = 500)
    @Comment("目标URL（商品详情页、分类页面等）")
    private String targetUrl;

    @Schema(description = "来源页面", example = "/pages/index/index")
    @Column(name = "sourcePage", length = 100)
    @Comment("来源页面（从哪个页面跳转过来的）")
    private String sourcePage;

    @Schema(description = "用户代理信息", example = "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15")
    @Column(name = "userAgent", length = 500)
    @Comment("用户代理信息（浏览器、设备信息等）")
    private String userAgent;

    @Schema(description = "IP地址", example = "192.168.1.100")
    @Column(name = "ipAddress", length = 50)
    @Comment("IP地址")
    private String ipAddress;

    @Schema(description = "会话ID", example = "session_001")
    @Column(name = "sessionId", length = 100)
    @Comment("会话ID")
    private String sessionId;

    @Schema(description = "停留时长（秒）", example = "30")
    @Column(name = "duration")
    @Comment("停留时长（秒）")
    private Integer duration;

    @Schema(description = "额外数据", example = "{\"categoryId\": 1, \"productPrice\": 29.90}")
    @Column(name = "extraData", columnDefinition = "json")
    @Comment("额外数据（JSON格式，存储其他相关信息）")
    private String extraData;

    @Schema(description = "创建时间")
    @Column(name = "createTime", nullable = false, updatable = false)
    @Comment("创建时间（自动生成，不可修改）")
    private LocalDateTime createTime = LocalDateTime.now();

    @Schema(description = "更新时间")
    @Column(name = "updateTime", nullable = false)
    @Comment("更新时间（自动更新）")
    private LocalDateTime updateTime = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
} 