-- 创建用户行为记录表
-- 使用数据库
USE agrishopper;

-- 删除已存在的表
DROP TABLE IF EXISTS `userBehavior`;

-- 创建用户行为记录表，使用UTF8MB4字符集支持完整Unicode字符
CREATE TABLE `userBehavior` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '行为记录ID，自增主键',
  `userId` bigint NOT NULL COMMENT '用户ID（关联wxuser表）',
  `behaviorType` varchar(50) NOT NULL COMMENT '行为类型（VIEW_PRODUCT-查看商品，CLICK_PRODUCT-点击商品，ADD_TO_CART-加入购物车，ADD_TO_FAVORITE-收藏商品，REMOVE_FROM_FAVORITE-取消收藏，SEARCH_PRODUCT-搜索商品，VIEW_CATEGORY-查看分类，VIEW_PAGE-浏览页面）',
  `targetType` varchar(50) NOT NULL COMMENT '目标类型（PRODUCT-商品，CATEGORY-分类，PAGE-页面，SEARCH-搜索）',
  `targetId` varchar(100) COMMENT '目标ID（商品编码、分类ID、页面路径等）',
  `targetName` varchar(500) COMMENT '目标名称（商品名称、分类名称、页面标题等）',
  `targetUrl` varchar(500) COMMENT '目标URL（商品详情页、分类页面等）',
  `sourcePage` varchar(100) COMMENT '来源页面（从哪个页面跳转过来的）',
  `userAgent` varchar(500) COMMENT '用户代理信息（浏览器、设备信息等）',
  `ipAddress` varchar(50) COMMENT 'IP地址',
  `sessionId` varchar(100) COMMENT '会话ID',
  `duration` int COMMENT '停留时长（秒）',
  `extraData` json COMMENT '额外数据（JSON格式，存储其他相关信息）',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（自动生成，不可修改）',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动更新）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`userId`) COMMENT '用户ID索引',
  KEY `idx_behavior_type` (`behaviorType`) COMMENT '行为类型索引',
  KEY `idx_target_type` (`targetType`) COMMENT '目标类型索引',
  KEY `idx_target_id` (`targetId`) COMMENT '目标ID索引',
  KEY `idx_create_time` (`createTime`) COMMENT '创建时间索引',
  KEY `idx_user_behavior_time` (`userId`, `behaviorType`, `createTime`) COMMENT '用户行为时间复合索引',
  CONSTRAINT `fk_userBehavior_user` FOREIGN KEY (`userId`) REFERENCES `wxuser` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户行为记录表';

-- 显示表结构
DESCRIBE `userBehavior`;

-- 插入一些测试数据
INSERT INTO `userBehavior` (
    `userId`, 
    `behaviorType`, 
    `targetType`, 
    `targetId`, 
    `targetName`, 
    `targetUrl`, 
    `sourcePage`, 
    `userAgent`, 
    `ipAddress`, 
    `sessionId`, 
    `duration`, 
    `extraData`
) VALUES 
(1, 'VIEW_PRODUCT', 'PRODUCT', 'P001', 'Organic Rice', '/pages/productDetail/productDetail?productCode=P001', '/pages/index/index', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15', '192.168.1.100', 'session_001', 30, '{"categoryId": 1, "productPrice": 29.90}'),
(1, 'CLICK_PRODUCT', 'PRODUCT', 'P002', 'Fresh Tomatoes', '/pages/productDetail/productDetail?productCode=P002', '/pages/productList/productList', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15', '192.168.1.100', 'session_001', 5, '{"categoryId": 2, "productPrice": 5.99}'),
(1, 'ADD_TO_CART', 'PRODUCT', 'P001', 'Organic Rice', '/pages/productDetail/productDetail?productCode=P001', '/pages/productDetail/productDetail', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15', '192.168.1.100', 'session_001', 2, '{"quantity": 2, "unitPrice": 29.90}'),
(1, 'ADD_TO_FAVORITE', 'PRODUCT', 'P003', 'Free-range Eggs', '/pages/productDetail/productDetail?productCode=P003', '/pages/productDetail/productDetail', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15', '192.168.1.100', 'session_001', 1, '{"categoryId": 3, "productPrice": 28.80}'),
(1, 'SEARCH_PRODUCT', 'SEARCH', 'rice', '搜索关键词：rice', '/pages/searchProduct/searchProduct?keyword=rice', '/pages/index/index', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15', '192.168.1.100', 'session_001', 15, '{"searchResults": 3, "keyword": "rice"}'),
(1, 'VIEW_CATEGORY', 'CATEGORY', '1', '农产品', '/pages/productList/productList?categoryId=1', '/pages/index/index', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15', '192.168.1.100', 'session_001', 45, '{"categoryName": "农产品", "productCount": 5}'),
(1, 'VIEW_PAGE', 'PAGE', '/pages/my/my', '我的页面', '/pages/my/my', '/pages/index/index', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15', '192.168.1.100', 'session_001', 60, '{"pageTitle": "我的", "userInfo": "已登录"}');

-- 显示创建结果
SELECT 'userBehavior表创建成功!' as message;
SELECT COUNT(*) as record_count FROM `userBehavior`; 