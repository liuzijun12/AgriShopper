CREATE DATABASE IF NOT EXISTS youlai_boot CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;


-- ----------------------------
-- 2. 创建表 && 数据初始化
-- ----------------------------
use youlai_boot;

SET NAMES utf8mb4;  # 设置字符集


SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `user` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
  `openid` VARCHAR(64) NOT NULL UNIQUE COMMENT '微信用户唯一ID',
  `unionid` VARCHAR(255) COMMENT '微信开放平台ID',
  `nickname` VARCHAR(64) COMMENT '微信昵称',
  `avatar` VARCHAR(250) COMMENT '头像URL',
  `real_name` VARCHAR(64) COMMENT '真实姓名',
  `phone` VARCHAR(20) COMMENT '手机号',
  `gender` TINYINT UNSIGNED COMMENT '性别',
  `province` VARCHAR(64) COMMENT '省份',
  `city` VARCHAR(64) COMMENT '城市',
  `district` VARCHAR(64) COMMENT '地区',
  `is_manager` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否是管理员',
  `is_supermanager` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否是超级管理员',
  `balance` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
  `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否软删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_time` DATETIME NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE UNIQUE INDEX `user_index_0` ON `user` (`id`, `openid`);

SET FOREIGN_KEY_CHECKS = 1;