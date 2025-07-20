    -- 更新用户地址表的字符集和注释
    -- 如果表已存在，执行以下SQL来更新字符集和注释

    -- 1. 更新表的字符集和排序规则
ALTER TABLE `userAddress` 
CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 更新表注释
ALTER TABLE `userAddress` 
COMMENT = '用户收货地址表，存储用户的收货地址信息';

    -- 3. 更新字段注释
    ALTER TABLE `userAddress` 
    MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID，主键自增',
    MODIFY COLUMN `userId` bigint(20) NOT NULL COMMENT '用户ID，关联用户表',
    MODIFY COLUMN `receiverName` varchar(255) NOT NULL COMMENT '收货人姓名，最大50个字符',
    MODIFY COLUMN `receiverPhone` varchar(255) NOT NULL COMMENT '收货人手机号，11位数字',
    MODIFY COLUMN `province` varchar(255) NOT NULL COMMENT '省份名称，如：河北省',
    MODIFY COLUMN `city` varchar(255) NOT NULL COMMENT '城市名称，如：张家口市',
    MODIFY COLUMN `district` varchar(255) NOT NULL COMMENT '区县名称，如：桥东区',
    MODIFY COLUMN `detailAddress` varchar(200) NOT NULL COMMENT '详细地址，如：胜利北路123号',
    MODIFY COLUMN `postalCode` varchar(10) DEFAULT NULL COMMENT '邮政编码，6位数字',
    MODIFY COLUMN `isDefault` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是，每个用户只能有一个默认地址',
    MODIFY COLUMN `isDeleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是，软删除标识',
    MODIFY COLUMN `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，自动设置当前时间',
    MODIFY COLUMN `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，自动更新为当前时间';

    -- 4. 更新索引注释（如果索引存在）
    -- 注意：MySQL不支持直接修改索引注释，需要删除重建
    -- 删除现有索引
    DROP INDEX IF EXISTS `idx_user_id` ON `userAddress`;
    DROP INDEX IF EXISTS `idx_is_default` ON `userAddress`;
    DROP INDEX IF EXISTS `idx_is_deleted` ON `userAddress`;

    -- 重新创建索引
    CREATE INDEX `idx_user_id` ON `userAddress` (`userId`) COMMENT '用户ID索引，用于快速查询用户地址';
    CREATE INDEX `idx_is_default` ON `userAddress` (`isDefault`) COMMENT '默认地址索引，用于快速查询默认地址';
    CREATE INDEX `idx_is_deleted` ON `userAddress` (`isDeleted`) COMMENT '删除状态索引，用于过滤已删除数据';

    -- 5. 验证更新结果
    -- 查看表结构
    DESCRIBE `userAddress`;

    -- 查看表的字符集
    SELECT 
        TABLE_NAME,
        TABLE_COLLATION,
        TABLE_COMMENT
    FROM 
        INFORMATION_SCHEMA.TABLES 
    WHERE 
        TABLE_SCHEMA = 'agrishopper' 
        AND TABLE_NAME = 'userAddress';

    -- 查看字段的字符集和注释
    SELECT 
        COLUMN_NAME,
        COLUMN_TYPE,
        CHARACTER_SET_NAME,
        COLLATION_NAME,
        COLUMN_COMMENT
    FROM 
        INFORMATION_SCHEMA.COLUMNS 
    WHERE 
        TABLE_SCHEMA = 'agrishopper' 
        AND TABLE_NAME = 'userAddress'
    ORDER BY 
        ORDINAL_POSITION; 