CREATE DATABASE IF NOT EXISTS youlai_boot CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;


-- ----------------------------
-- 2. 创建表 && 数据初始化
-- ----------------------------
use youlai_boot;

SET NAMES utf8mb4;  # 设置字符集

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `product_cart` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
  `user_id` INTEGER UNSIGNED NOT NULL COMMENT '用户唯一标识',
  `product_id` INTEGER UNSIGNED NOT NULL COMMENT '商品ID',
  `product_type` VARCHAR(255) NOT NULL COMMENT '选择商品的规格',
  `product_count` INTEGER UNSIGNED NOT NULL DEFAULT 1 COMMENT '商品的数量',
  `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否软删除',
  `product_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '所选规格的价格',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_time` DATETIME NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车';

CREATE UNIQUE INDEX `product_cart_index_0` ON `product_cart` (`product_id`, `product_type`, `user_id`);

ALTER TABLE `product_cart`
  ADD CONSTRAINT `fk_cart_product`
  FOREIGN KEY(`product_id`) REFERENCES `product`(`id`)
  ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE `product_cart`
  ADD CONSTRAINT `fk_cart_user`
  FOREIGN KEY(`user_id`) REFERENCES `user`(`id`)
  ON UPDATE CASCADE ON DELETE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;
