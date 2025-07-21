-- 客服对话相关数据库表初始化脚本
-- 使用utf8mb4编码，适用于Docker环境，采用小驼峰命名法

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection = utf8mb4;

-- 使用数据库
USE agrishopper;

-- 1. 客服对话会话表
CREATE TABLE IF NOT EXISTS customerservicesession (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会话ID',
    sessionCode VARCHAR(50) NOT NULL UNIQUE COMMENT '会话编码（唯一标识）',
    userId BIGINT NOT NULL COMMENT '用户ID（关联wxuser表）',
    productId BIGINT COMMENT '关联产品ID（可选，产品咨询时关联）',
    orderId VARCHAR(50) COMMENT '关联订单号（可选，订单咨询时关联）',
    sessionType TINYINT NOT NULL DEFAULT 1 COMMENT '会话类型：1-产品咨询，2-订单咨询，3-售后服务，4-其他',
    sessionStatus TINYINT NOT NULL DEFAULT 1 COMMENT '会话状态：1-进行中，2-已结束，3-已关闭',
    priority TINYINT NOT NULL DEFAULT 2 COMMENT '优先级：1-高，2-中，3-低',
    assignedAgentId BIGINT COMMENT '分配的客服ID（关联admin表）',
    autoReplyEnabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用自动回复',
    aiEnabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用AI助手',
    aiModel VARCHAR(50) COMMENT '使用的AI模型名称',
    lastMessageTime DATETIME COMMENT '最后消息时间',
    createTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDeleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '软删除标记',
    
    -- 添加索引
    INDEX idx_sessionCode (sessionCode),
    INDEX idx_userId (userId),
    INDEX idx_productId (productId),
    INDEX idx_orderId (orderId),
    INDEX idx_sessionStatus (sessionStatus),
    INDEX idx_assignedAgentId (assignedAgentId),
    INDEX idx_createTime (createTime),
    
    -- 外键约束
    FOREIGN KEY (userId) REFERENCES wxuser(id) ON DELETE CASCADE,
    FOREIGN KEY (productId) REFERENCES products(id) ON DELETE SET NULL,
    FOREIGN KEY (assignedAgentId) REFERENCES admins(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客服对话会话表';

-- 2. 客服消息表
CREATE TABLE IF NOT EXISTS customerservicemessage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    sessionId BIGINT NOT NULL COMMENT '会话ID（关联customerservicesession表）',
    senderType TINYINT NOT NULL COMMENT '发送者类型：1-用户，2-客服，3-系统，4-AI助手',
    senderId BIGINT COMMENT '发送者ID（用户ID或客服ID）',
    messageType TINYINT NOT NULL DEFAULT 1 COMMENT '消息类型：1-文本，2-图片，3-语音，4-文件，5-系统通知',
    messageContent TEXT NOT NULL COMMENT '消息内容',
    messageMediaUrl VARCHAR(500) COMMENT '媒体文件URL（图片、语音、文件等）',
    messageDuration INT COMMENT '语音消息时长（秒）',
    isRead BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已读',
    readTime DATETIME COMMENT '阅读时间',
    aiGenerated BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否为AI生成的消息',
    aiConfidence DECIMAL(3,2) COMMENT 'AI回复的置信度（0-1）',
    aiModel VARCHAR(50) COMMENT '生成此消息的AI模型',
    createTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    isDeleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '软删除标记',
    
    -- 添加索引
    INDEX idx_sessionId (sessionId),
    INDEX idx_senderType (senderType),
    INDEX idx_senderId (senderId),
    INDEX idx_messageType (messageType),
    INDEX idx_isRead (isRead),
    INDEX idx_aiGenerated (aiGenerated),
    INDEX idx_createTime (createTime),
    
    -- 外键约束
    FOREIGN KEY (sessionId) REFERENCES customerservicesession(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客服消息表';

-- 插入一些测试数据

-- 插入测试会话数据
INSERT INTO customerservicesession (sessionCode, userId, productId, sessionType, sessionStatus, priority) VALUES
('CS20241201001', 1, 7, 1, 1, 2),
('CS20241201002', 1, 8, 1, 1, 2),
('CS20241201003', 1, NULL, 2, 1, 1);

-- 插入测试消息数据
INSERT INTO customerservicemessage (sessionId, senderType, senderId, messageType, messageContent, aiGenerated, aiConfidence) VALUES
(1, 1, 1, 1, '这个番茄的价格是多少？', FALSE, NULL),
(1, 4, NULL, 1, '您好！新鲜番茄的价格是5.99元/斤，目前有现货供应。请问您需要多少斤呢？', TRUE, 0.95),
(1, 1, 1, 1, '我要买3斤', FALSE, NULL),
(1, 4, NULL, 1, '好的！3斤新鲜番茄总计17.97元。您可以直接在商品页面下单，我们会尽快为您发货。', TRUE, 0.92),
(2, 1, 1, 1, '黄瓜什么时候发货？', FALSE, NULL),
(2, 4, NULL, 1, '新鲜黄瓜通常在订单确认后1-2个工作日内发货，预计3-5天送达。', TRUE, 0.88);

-- 显示创建结果
SELECT 'Customer service tables created successfully!' as message;
SELECT COUNT(*) as session_count FROM customerservicesession;
SELECT COUNT(*) as message_count FROM customerservicemessage; 