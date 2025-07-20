-- 创建用户地址表
CREATE TABLE IF NOT EXISTS `userAddress` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID，主键自增',
  `userId` bigint(20) NOT NULL COMMENT '用户ID，关联用户表',
  `receiverName` varchar(255) NOT NULL COMMENT '收货人姓名，最大50个字符',
  `receiverPhone` varchar(255) NOT NULL COMMENT '收货人手机号，11位数字',
  `province` varchar(255) NOT NULL COMMENT '省份名称，如：河北省',
  `city` varchar(255) NOT NULL COMMENT '城市名称，如：张家口市',
  `district` varchar(255) NOT NULL COMMENT '区县名称，如：桥东区',
  `detailAddress` varchar(200) NOT NULL COMMENT '详细地址，如：胜利北路123号',
  `postalCode` varchar(10) DEFAULT NULL COMMENT '邮政编码，6位数字',
  `isDefault` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是，每个用户只能有一个默认地址',
  `isDeleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是，软删除标识',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，自动设置当前时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，自动更新为当前时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`userId`) COMMENT '用户ID索引，用于快速查询用户地址',
  KEY `idx_is_default` (`isDefault`) COMMENT '默认地址索引，用于快速查询默认地址',
  KEY `idx_is_deleted` (`isDeleted`) COMMENT '删除状态索引，用于过滤已删除数据'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收货地址表，存储用户的收货地址信息';

-- 插入测试数据
INSERT INTO `userAddress` (`userId`, `receiverName`, `receiverPhone`, `province`, `city`, `district`, `detailAddress`, `postalCode`, `isDefault`) VALUES
(1, '张三', '13800138001', '河北省', '张家口市', '桥东区', '胜利北路123号', '075000', 1),
(1, '李四', '13800138002', '河北省', '张家口市', '桥西区', '建设街456号', '075000', 0),
(1, '王五', '13800138003', '河北省', '张家口市', '宣化区', '南大街789号', '075100', 0),
(2, '赵六', '13800138004', '河北省', '张家口市', '下花园区', '花园路321号', '075300', 1),
(2, '钱七', '13800138005', '河北省', '张家口市', '万全区', '万全路654号', '076250', 0); 