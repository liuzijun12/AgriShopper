-- AgriShopper 数据库初始化脚本
-- 适用于Docker环境

-- 创建数据库
CREATE DATABASE IF NOT EXISTS agrishopper 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE agrishopper;

-- 创建商品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID，自增主键',
    productCode VARCHAR(50) NOT NULL UNIQUE COMMENT '商品编码（唯一）',
    productName VARCHAR(255) NOT NULL COMMENT '商品名称',
    productDescription TEXT COMMENT '商品描述（文本类型）',
    categoryId BIGINT NOT NULL COMMENT '所属分类ID（必填）',
    subCategoryId BIGINT COMMENT '子分类ID（可选）',
    supplierId BIGINT NOT NULL COMMENT '供应商ID（必填）',
    productUnit VARCHAR(20) COMMENT '计量单位（如：斤、袋、箱）',
    productSpec VARCHAR(100) COMMENT '规格描述（如：500g/袋）',
    productPrice DECIMAL(10,2) NOT NULL COMMENT '销售价（必填，精确到小数点后2位）',
    costPrice DECIMAL(10,2) COMMENT '成本价（可选）',
    stockQuantity INT NOT NULL DEFAULT 0 COMMENT '当前库存（默认0）',
    minOrderQuantity INT NOT NULL DEFAULT 1 COMMENT '最小起订量（默认1）',
    isHotProduct BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否热销商品（默认false）',
    isNewProduct BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否新品（默认false）',
    productStatus INT NOT NULL DEFAULT 1 COMMENT '状态（1-上架，0-下架，默认1）',
    mainImageUrl VARCHAR(500) COMMENT '主图URL（可选）',
    createTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（自动生成，不可修改）',
    updateTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动更新）',
    
    -- 添加索引
    INDEX idx_productCode (productCode),
    INDEX idx_categoryId (categoryId),
    INDEX idx_supplierId (supplierId),
    INDEX idx_productStatus (productStatus),
    INDEX idx_createTime (createTime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 插入一些测试数据
INSERT INTO products (
    productCode, 
    productName, 
    productDescription, 
    categoryId, 
    supplierId, 
    productUnit, 
    productSpec, 
    productPrice, 
    stockQuantity, 
    isHotProduct, 
    isNewProduct, 
    productStatus, 
    mainImageUrl
) VALUES 
('P001', 'Organic Rice', 'Farm-grown organic rice, no pesticides or chemical fertilizers, sweet taste', 1, 1, 'bag', '5kg/bag', 29.90, 100, TRUE, FALSE, 1, 'https://example.com/images/rice.jpg'),
('P002', 'Fresh Tomatoes', 'Seasonal fresh tomatoes, sweet and sour, nutritious', 2, 1, 'jin', '500g/piece', 5.99, 50, TRUE, TRUE, 1, 'https://example.com/images/tomato.jpg'),
('P003', 'Free-range Eggs', 'Eggs from free-range chickens, rich yolk, nutritious', 3, 2, 'box', '30 pieces/box', 28.80, 30, FALSE, FALSE, 1, 'https://example.com/images/eggs.jpg'),
('P004', 'Organic Sweet Potato', 'Farm-grown, sweet taste, rich in dietary fiber', 2, 1, 'jin', '1kg/piece', 15.90, 80, FALSE, TRUE, 1, 'https://example.com/images/sweet-potato.jpg'),
('P005', 'Fresh Corn', 'Seasonal harvest, plump kernels, moderate sweetness', 2, 1, 'piece', 'about 300g per piece', 9.90, 60, TRUE, FALSE, 1, 'https://example.com/images/corn.jpg');

-- 显示创建结果
SELECT 'Database and tables created successfully!' as message;
SELECT COUNT(*) as product_count FROM products; 