CREATE DATABASE IF NOT EXISTS youlai_boot CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;


-- ----------------------------
-- 2. 创建表 && 数据初始化
-- ----------------------------
use youlai_boot;

SET NAMES utf8mb4;  # 设置字符集

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `address` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
  `user_id` INTEGER UNSIGNED NOT NULL COMMENT '关联用户ID',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `province` VARCHAR(20) NOT NULL COMMENT '省份',
  `city` VARCHAR(20) NOT NULL COMMENT '城市',
  `district` VARCHAR(20) NOT NULL COMMENT '区县',
  `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
  `postal_code` VARCHAR(10) COMMENT '邮政编码',
  `is_default` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否默认地址',
  `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否软删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_time` DATETIME NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='存储用户收货地址信息';

CREATE INDEX `address_index_0` ON `address` (`user_id`);

ALTER TABLE `address`
  ADD CONSTRAINT `fk_addr_user`
  FOREIGN KEY(`user_id`) REFERENCES `user`(`id`)
  ON UPDATE CASCADE ON DELETE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;
