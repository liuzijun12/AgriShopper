-- 创建收藏表
-- 使用数据库
USE agrishopper;

-- 删除已存在的表
DROP TABLE IF EXISTS `favorites`;

-- 创建收藏表，使用UTF8MB4字符集支持完整Unicode字符
-- 修改为使用商品编号（productCode）而不是商品ID
CREATE TABLE `favorites` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID，自增主键',
  `userId` bigint NOT NULL COMMENT '用户ID（关联wxuser表）',
  `productCode` varchar(50) NOT NULL COMMENT '商品编号（关联products表的productCode）',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（自动生成，不可修改）',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动更新）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`userId`, `productCode`) COMMENT '用户商品唯一索引，防止重复收藏',
  KEY `idx_user_id` (`userId`) COMMENT '用户ID索引',
  KEY `idx_product_code` (`productCode`) COMMENT '商品编号索引',
  KEY `idx_create_time` (`createTime`) COMMENT '创建时间索引',
  CONSTRAINT `fk_favorites_user` FOREIGN KEY (`userId`) REFERENCES `wxuser` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 显示表结构
DESCRIBE `favorites`; 