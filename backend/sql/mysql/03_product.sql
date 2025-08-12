CREATE TABLE `product` (
                           `id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
                           `name` VARCHAR(255) COMMENT '名称',
                           `description` VARCHAR(255) COMMENT '商品的描述',
                           `price` DECIMAL COMMENT '价格',
                           `has_discount` BOOLEAN COMMENT '是否有优惠',
                           `discount_price` DECIMAL COMMENT '优惠后的价格',
                           `is_hot` BOOLEAN COMMENT '是否推荐产品',
                           `sales` INTEGER COMMENT '销量',
                           `virtual_sales` INTEGER COMMENT '虚拟销量',
                           `origin` VARCHAR(255) COMMENT '产品地',
                           `stock` BIGINT COMMENT '库存',
                           `is_online` BOOLEAN COMMENT '是否上架',
                           `type` JSON COMMENT '规格',
                           `images` JSON COMMENT '照片',
                           `vedio` JSON COMMENT '视频',
                           `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY(`id`)
) COMMENT='商品表';


CREATE INDEX `product_index_0`
    ON `product` (`id`);
CREATE TABLE `product_tags` (
                                `id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
                                `name` VARCHAR(255) COMMENT '标签名',
                                `parent_id` INTEGER,
                                `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY(`id`)
) COMMENT='标签';


CREATE TABLE `id_tags` (
                           `id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
                           `product_id` INTEGER COMMENT '商品ID',
                           `tags_id` INTEGER COMMENT '标签ID',
                           `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品标签关联表';


CREATE INDEX `id_tags_index_0`
    ON `id_tags` (`tags_id`);
CREATE INDEX `id_tags_index_1`
    ON `id_tags` (`product_id`);
CREATE TABLE `product_category` (
                                    `id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
                                    `name` VARCHAR(255) COMMENT '分类名称',
                                    `icon` VARCHAR(255) COMMENT '分类图释',
                                    `parent_id` INTEGER COMMENT '一级id',
                                    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY(`id`)
) COMMENT='分类表';


CREATE TABLE `id_category` (
                               `id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
                               `product_id` INTEGER,
                               `category_id` INTEGER,
                               `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY(`id`)
) COMMENT='分类关联表';


CREATE INDEX `id_category_index_0`
    ON `id_category` (`category_id`);
CREATE INDEX `id_category_index_1`
    ON `id_category` (`product_id`);
ALTER TABLE `id_tags`
    ADD FOREIGN KEY(`tags_id`) REFERENCES `product_tags`(`id`)
        ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE `id_category`
    ADD FOREIGN KEY(`category_id`) REFERENCES `product_category`(`id`)
        ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE `id_tags`
    ADD FOREIGN KEY(`product_id`) REFERENCES `product`(`id`)
        ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE `id_category`
    ADD FOREIGN KEY(`product_id`) REFERENCES `product`(`id`)
        ON UPDATE CASCADE ON DELETE CASCADE;