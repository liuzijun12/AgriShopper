-- 创建分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID，自增主键',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    category_description TEXT COMMENT '分类描述',
    category_icon VARCHAR(500) COMMENT '分类图标URL',
    sort_order INT NOT NULL DEFAULT 999 COMMENT '排序权重（数字越小越靠前）',
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用（默认true）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（自动生成，不可修改）',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动更新）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 插入初始分类数据
INSERT INTO categories (category_name, category_description, sort_order, is_enabled) VALUES
('农产品', '新鲜农产品，包括蔬菜、水果等', 1, true),
('养生保健', '保健品、中药材等健康产品', 2, true),
('饲料', '动物饲料、养殖用品等', 3, true),
('农具', '农业工具、机械设备等', 4, true),
('种子', '各类农作物种子', 5, true);

-- 更新现有商品的分类ID，使其与新的分类表对应
-- 根据之前的查询结果，更新商品分类
UPDATE products SET category_id = 1 WHERE product_name IN ('黄瓜', '土豆', '西瓜'); -- 农产品
UPDATE products SET category_id = 2 WHERE product_name = '保健品'; -- 养生保健
UPDATE products SET category_id = 3 WHERE product_name = '饲料'; -- 饲料
UPDATE products SET category_id = 1 WHERE product_name = '番茄'; -- 番茄也归类为农产品 