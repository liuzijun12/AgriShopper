-- 为商品表添加销量字段
USE agrishopper;

-- 添加销量字段
ALTER TABLE products ADD COLUMN salesCount INT NOT NULL DEFAULT 0 COMMENT '销量（默认0）' AFTER stockQuantity;

-- 添加销量索引以提高查询性能
ALTER TABLE products ADD INDEX idx_salesCount (salesCount);

-- 更新现有商品的销量数据（模拟一些销量数据）
UPDATE products SET salesCount = 150 WHERE productCode = 'P001';  -- 有机大米销量最高
UPDATE products SET salesCount = 120 WHERE productCode = 'P002';  -- 新鲜番茄
UPDATE products SET salesCount = 80 WHERE productCode = 'P003';   -- 散养鸡蛋
UPDATE products SET salesCount = 95 WHERE productCode = 'P004';   -- 有机红薯
UPDATE products SET salesCount = 110 WHERE productCode = 'P005';  -- 新鲜玉米

-- 显示更新结果
SELECT productCode, productName, stockQuantity, salesCount FROM products ORDER BY salesCount DESC; 