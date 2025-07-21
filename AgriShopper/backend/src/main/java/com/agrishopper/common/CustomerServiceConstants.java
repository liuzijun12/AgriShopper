package com.agrishopper.common;

/**
 * 客服相关常量
 */
public class CustomerServiceConstants {
    
    /**
     * 会话类型
     */
    public static class SessionType {
        public static final Integer PRODUCT_INQUIRY = 1;    // 产品咨询
        public static final Integer ORDER_INQUIRY = 2;      // 订单咨询
        public static final Integer AFTER_SALES = 3;        // 售后服务
        public static final Integer OTHER = 4;              // 其他
    }
    
    /**
     * 会话状态
     */
    public static class SessionStatus {
        public static final Integer ACTIVE = 1;             // 进行中
        public static final Integer ENDED = 2;              // 已结束
        public static final Integer CLOSED = 3;             // 已关闭
    }
    
    /**
     * 优先级
     */
    public static class Priority {
        public static final Integer HIGH = 1;               // 高
        public static final Integer MEDIUM = 2;             // 中
        public static final Integer LOW = 3;                // 低
    }
    
    /**
     * 发送者类型
     */
    public static class SenderType {
        public static final Integer USER = 1;               // 用户
        public static final Integer AGENT = 2;              // 客服
        public static final Integer SYSTEM = 3;             // 系统
        public static final Integer AI = 4;                 // AI助手
    }
    
    /**
     * 消息类型
     */
    public static class MessageType {
        public static final Integer TEXT = 1;               // 文本
        public static final Integer IMAGE = 2;              // 图片
        public static final Integer VOICE = 3;              // 语音
        public static final Integer FILE = 4;               // 文件
        public static final Integer SYSTEM_NOTIFICATION = 5; // 系统通知
    }
    
    /**
     * 会话编码前缀
     */
    public static final String SESSION_CODE_PREFIX = "CS";
    
    /**
     * 默认欢迎消息
     */
    public static final String DEFAULT_WELCOME_MESSAGE = "您好！欢迎来到AgriShopper客服中心，我是您的专属客服。请问有什么可以帮助您的吗？";
    
    /**
     * 转接消息
     */
    public static final String TRANSFER_MESSAGE = "正在为您转接人工客服，请稍候...";
    
    /**
     * 会话结束消息
     */
    public static final String SESSION_END_MESSAGE = "感谢您的咨询，会话已结束。如有其他问题，欢迎随时联系我们！";
} 