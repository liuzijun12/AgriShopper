CREATE DATABASE IF NOT EXISTS youlai_boot CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;


-- ----------------------------
-- 2. 创建表 && 数据初始化
-- ----------------------------
use youlai_boot;

SET NAMES utf8mb4;  # 设置字符集


SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
  `user_id` INTEGER UNSIGNED NOT NULL COMMENT '识别用户的唯一标识',
  `product_id` INTEGER NOT NULL COMMENT '商品的id',
  `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否软删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_time` DATETIME NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

CREATE UNIQUE INDEX `user_favorite_index_0` ON `user_favorite` (`product_id`, `user_id`);

ALTER TABLE `user_favorite`
  ADD CONSTRAINT `fk_fav_product`
  FOREIGN KEY(`product_id`) REFERENCES `product`(`id`)
  ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE `user_favorite`
  ADD CONSTRAINT `fk_fav_user`
  FOREIGN KEY(`user_id`) REFERENCES `user`(`id`)
  ON UPDATE CASCADE ON DELETE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;
