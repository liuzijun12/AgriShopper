-- AgriShopper shoppingcart表结构脚本
-- 适用于Docker环境

-- 使用数据库
USE agrishopper;

-- 创建shoppingcart表（支持多商品）
CREATE TABLE IF NOT EXISTS shoppingcart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车项ID',
    userId BIGINT NOT NULL COMMENT '用户ID',
    productId BIGINT NOT NULL COMMENT '商品ID',
    selectedSpec VARCHAR(100) COMMENT '用户选择的规格（如：红色/XL）',
    quantity INT NOT NULL DEFAULT 1 COMMENT '商品数量（默认1）',
    selected BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否选中（默认true）',
    isDeleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除（默认false，1-正常，0-已删除）',
    addedTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    updateTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 唯一约束：同一用户同商品同规格只能有一条记录
    UNIQUE KEY uk_user_product_spec (userId, productId, selectedSpec),
    
    -- 外键约束（确保商品存在）
    CONSTRAINT fk_cart_product
        FOREIGN KEY (productId)
        REFERENCES products(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    
    -- 索引优化
    INDEX idx_userId (userId),
    INDEX idx_updateTime (updateTime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='shoppingcart表';

-- 验证表创建成功
SELECT 'shoppingcart表创建成功!' as message;
    