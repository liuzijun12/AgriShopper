SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `orders` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
  `user_id` INTEGER UNSIGNED NOT NULL COMMENT '关联用户ID',
  `address_snapshot` JSON NOT NULL COMMENT '完整地址快照',
  `original_address_id` INTEGER UNSIGNED NOT NULL COMMENT '关联原始地址',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '订单的状态',
  `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
  `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否软删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_time` DATETIME NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

CREATE INDEX `orders_index_0` ON `orders` (`user_id`);
CREATE INDEX `idx_orders_status_created_at` ON `orders` (`status`, `create_time`);

ALTER TABLE `orders`
  ADD CONSTRAINT `fk_orders_user`
  FOREIGN KEY(`user_id`) REFERENCES `user`(`id`)
  ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE `orders`
  ADD CONSTRAINT `fk_orders_address`
  FOREIGN KEY(`original_address_id`) REFERENCES `address`(`id`)
  ON UPDATE CASCADE ON DELETE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;
