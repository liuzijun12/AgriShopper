SET NAMES utf8mb4;

INSERT INTO `sys_menu` (`id`, `parent_id`, `tree_path`, `name`, `type`, `route_name`, `route_path`, `component`, `perm`, `always_show`, `keep_alive`, `visible`, `sort`, `icon`, `redirect`, `create_time`, `update_time`, `params`) VALUES
(149,	0,	'0',	'商品',	2,	NULL,	'/system',	'Layout',	NULL,	1,	1,	1,	3,	'el-icon-Shop',	NULL,	'2025-08-09 19:11:34',	'2025-08-13 19:56:18',	NULL),
(150,	149,	'0,149',	'商品管理',	1,	'SystemProduct',	'system/product',	'system/product/index',	NULL,	1,	1,	1,	1,	'el-icon-IceDrink',	NULL,	'2025-08-09 19:14:24',	'2025-08-13 20:14:46',	NULL),
(151,	149,	'0,149',	'分类',	1,	'SystemCategory',	'system/productCategory',	'system/productCategory/index',	NULL,	1,	1,	1,	1,	'el-icon-Menu',	NULL,	'2025-08-09 19:15:08',	'2025-08-13 20:15:23',	NULL),
(152,	149,	'0,149',	'标签',	1,	'SystemProductTags',	'system/productTags',	'system/productTags/index',	NULL,	1,	1,	1,	1,	'el-icon-Medal',	NULL,	'2025-08-09 19:16:48',	'2025-08-13 20:15:52',	NULL),
(156,	0,	'0',	'微信用户管理',	2,	'wxUser',	'/system',	'Layout',	NULL,	1,	1,	1,	3,	'wechat',	NULL,	'2025-08-13 17:12:05',	'2025-08-13 21:21:53',	NULL),
(157,	156,	'0,156',	'wxuser',	1,	'wxUser1',	'/system/wxuser',	'system/wxuser/index',	NULL,	0,	1,	1,	1,	'el-icon-Avatar',	NULL,	'2025-08-13 17:16:35',	'2025-08-13 20:14:29',	NULL),
(158,	156,	'0,156',	'用户收藏查看',	1,	'userfavorite',	'/system/userFavorite',	'system/userFavorite/index',	NULL,	0,	1,	1,	1,	'el-icon-StarFilled',	NULL,	'2025-08-13 20:13:40',	'2025-08-13 21:10:44',	NULL),
(159,	156,	'0,156',	'用户购物车查看',	1,	'productCart',	'/system/productCart',	'system/productCart/index',	NULL,	0,	1,	1,	1,	'el-icon-ShoppingCartFull',	NULL,	'2025-08-13 21:20:57',	'2025-08-13 23:41:39',	NULL);
-- 2025-08-15 04:16:15 UTC

