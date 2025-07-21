-- Create user behavior record table
-- Use database
USE agrishopper;

-- Drop existing table
DROP TABLE IF EXISTS `userBehavior`;

-- Create user behavior record table with UTF8MB4 charset
CREATE TABLE `userBehavior` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Behavior record ID',
  `userId` bigint NOT NULL COMMENT 'User ID (reference wxuser table)',
  `behaviorType` varchar(50) NOT NULL COMMENT 'Behavior type (VIEW_PRODUCT, CLICK_PRODUCT, ADD_TO_CART, etc.)',
  `targetType` varchar(50) NOT NULL COMMENT 'Target type (PRODUCT, CATEGORY, PAGE, SEARCH)',
  `targetId` varchar(100) COMMENT 'Target ID (product code, category ID, page path, etc.)',
  `targetName` varchar(500) COMMENT 'Target name (product name, category name, page title, etc.)',
  `targetUrl` varchar(500) COMMENT 'Target URL (product detail page, category page, etc.)',
  `sourcePage` varchar(100) COMMENT 'Source page (from which page)',
  `userAgent` varchar(500) COMMENT 'User agent information',
  `ipAddress` varchar(50) COMMENT 'IP address',
  `sessionId` varchar(100) COMMENT 'Session ID',
  `duration` int COMMENT 'Duration in seconds',
  `extraData` json COMMENT 'Extra data (JSON format)',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`userId`) COMMENT 'User ID index',
  KEY `idx_behavior_type` (`behaviorType`) COMMENT 'Behavior type index',
  KEY `idx_target_type` (`targetType`) COMMENT 'Target type index',
  KEY `idx_target_id` (`targetId`) COMMENT 'Target ID index',
  KEY `idx_create_time` (`createTime`) COMMENT 'Create time index',
  KEY `idx_user_behavior_time` (`userId`, `behaviorType`, `createTime`) COMMENT 'User behavior time composite index',
  CONSTRAINT `fk_userBehavior_user` FOREIGN KEY (`userId`) REFERENCES `wxuser` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='User behavior record table';

-- Show table structure
DESCRIBE `userBehavior`;

-- Insert test data
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
(1, 'SEARCH_PRODUCT', 'SEARCH', 'rice', 'Search keyword: rice', '/pages/searchProduct/searchProduct?keyword=rice', '/pages/index/index', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15', '192.168.1.100', 'session_001', 15, '{"searchResults": 3, "keyword": "rice"}'),
(1, 'VIEW_CATEGORY', 'CATEGORY', '1', 'Agricultural Products', '/pages/productList/productList?categoryId=1', '/pages/index/index', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15', '192.168.1.100', 'session_001', 45, '{"categoryName": "Agricultural Products", "productCount": 5}'),
(1, 'VIEW_PAGE', 'PAGE', '/pages/my/my', 'My Page', '/pages/my/my', '/pages/index/index', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15', '192.168.1.100', 'session_001', 60, '{"pageTitle": "My", "userInfo": "Logged in"}');

-- Show creation result
SELECT 'userBehavior table created successfully!' as message;
SELECT COUNT(*) as record_count FROM `userBehavior`; 