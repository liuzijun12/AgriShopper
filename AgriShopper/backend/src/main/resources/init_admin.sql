-- 创建管理员表
CREATE TABLE IF NOT EXISTS admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '管理员ID，自增主键',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（唯一）',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    realName VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱地址',
    phone VARCHAR(20) COMMENT '手机号码',
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN' COMMENT '角色（ADMIN-管理员，SUPER_ADMIN-超级管理员）',
    isEnabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用（默认true）',
    lastLoginTime DATETIME COMMENT '最后登录时间',
    createTime DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间（自动生成，不可修改）',
    updateTime DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间（自动更新）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员用户表';

-- 插入默认管理员账户（密码：admin123）
-- 注意：这里的密码是BCrypt加密后的值，实际密码是 admin123
INSERT INTO admins (username, password, realName, email, role, isEnabled) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '系统管理员', 'admin@agrishopper.com', 'SUPER_ADMIN', true),
('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '商品管理员', 'manager@agrishopper.com', 'ADMIN', true); 