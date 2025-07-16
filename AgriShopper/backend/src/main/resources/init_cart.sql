-- 创建shoppingcart表
CREATE TABLE IF NOT EXISTS `shoppingcart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车项ID，自增主键',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联wxuser表）',
  `product_id` bigint NOT NULL COMMENT '商品ID（关联products表）',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '商品数量（默认1）',
  `unit_price` decimal(10,2) NOT NULL COMMENT '商品单价（精确到小数点后2位）',
  `subtotal` decimal(10,2) NOT NULL COMMENT '小计金额（单价×数量）',
  `is_selected` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否选中（默认true）',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除（默认false，1-正常，0-已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（自动生成，不可修改）',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动更新）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_product` (`user_id`, `product_id`),
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `wxuser` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='shoppingcart表';

-- 插入一些测试数据（可选）
-- INSERT INTO `shoppingcart` (`user_id`, `product_id`, `quantity`, `unit_price`, `subtotal`, `is_selected`) VALUES
-- (1, 1, 2, 29.90, 59.80, 1),
-- (1, 2, 1, 45.90, 45.90, 1),
-- (1, 3, 3, 38.50, 115.50, 0); 