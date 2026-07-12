-- ============================================
-- 江右拾遗 - 数据库初始化脚本
-- 在 Navicat 中执行：先创建数据库，然后运行此脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS jiangyou_shiyi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE jiangyou_shiyi;

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `account` VARCHAR(64) NOT NULL UNIQUE COMMENT '账号',
  `openid` VARCHAR(64) NULL UNIQUE COMMENT '微信openid',
  `password` VARCHAR(128) DEFAULT '' COMMENT '密码',
  `nick_name` VARCHAR(64) DEFAULT '' COMMENT '昵称',
  `avatar_url` VARCHAR(512) DEFAULT '' COMMENT '头像URL',
  `gender` VARCHAR(8) DEFAULT '' COMMENT '性别',
  `bio` VARCHAR(256) DEFAULT '' COMMENT '简介',
  `location` VARCHAR(128) DEFAULT '' COMMENT '所在地',
  `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '模拟余额',
  `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
  `join_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `last_login` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后登录',
  `follow_count` INT DEFAULT 0 COMMENT '关注数',
  `follower_count` INT DEFAULT 0 COMMENT '粉丝数',
  `checkin_count` INT DEFAULT 0 COMMENT '打卡总数',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1正常 0禁用',
  INDEX `idx_account` (`account`),
  INDEX `idx_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 非遗项目表
-- ----------------------------
DROP TABLE IF EXISTS `heritage`;
CREATE TABLE `heritage` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '非遗ID',
  `name` VARCHAR(128) NOT NULL COMMENT '名称',
  `alias` VARCHAR(128) DEFAULT '' COMMENT '别名',
  `category` VARCHAR(32) NOT NULL COMMENT '分类（传统技艺/传统戏剧/民俗...）',
  `level` VARCHAR(16) DEFAULT '' COMMENT '级别（国家级/省级/市级）',
  `batch` VARCHAR(32) DEFAULT '' COMMENT '批次',
  `city` VARCHAR(32) DEFAULT '' COMMENT '地市',
  `county` VARCHAR(32) DEFAULT '' COMMENT '区县',
  `latitude` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
  `longitude` DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
  `summary` TEXT COMMENT '简介',
  `description` TEXT COMMENT '详细介绍',
  `history` TEXT COMMENT '历史渊源',
  `features` TEXT COMMENT '特色',
  `cover_image` VARCHAR(512) DEFAULT '' COMMENT '封面图URL',
  `images` TEXT COMMENT '图片列表JSON',
  `tags` VARCHAR(256) DEFAULT '' COMMENT '标签（逗号分隔）',
  `travel_tips` TEXT COMMENT '探访攻略',
  `visit_hours` VARCHAR(128) DEFAULT '' COMMENT '开放时间',
  `ticket_info` VARCHAR(256) DEFAULT '' COMMENT '门票信息',
  `checkin_count` INT DEFAULT 0 COMMENT '打卡人数',
  `view_count` INT DEFAULT 0 COMMENT '浏览量',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1上架 0下架',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_category` (`category`),
  INDEX `idx_city` (`city`),
  INDEX `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='非遗项目表';

-- ----------------------------
-- 商品表
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
  `heritage_id` BIGINT DEFAULT NULL COMMENT '关联非遗ID',
  `name` VARCHAR(128) NOT NULL COMMENT '商品名称',
  `description` TEXT COMMENT '商品描述',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  `original_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '原价',
  `stock` INT DEFAULT 0 COMMENT '库存',
  `images` TEXT COMMENT '商品图片JSON数组',
  `specs` TEXT COMMENT '规格JSON',
  `category` VARCHAR(32) DEFAULT '' COMMENT '商品分类',
  `seller` VARCHAR(64) DEFAULT '' COMMENT '售卖人/传承人',
  `seller_avatar` VARCHAR(512) DEFAULT '' COMMENT '传承人头像',
  `sales` INT DEFAULT 0 COMMENT '销量',
  `rating` DECIMAL(2,1) DEFAULT 5.0 COMMENT '评分',
  `tags` VARCHAR(256) DEFAULT '' COMMENT '标签',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1上架 0下架',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_category` (`category`),
  INDEX `idx_heritage` (`heritage_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- 订单表
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额',
  `status` VARCHAR(16) DEFAULT 'pending' COMMENT '状态 pending/paid/shipped/received/completed/cancelled',
  `consignee` VARCHAR(32) DEFAULT '' COMMENT '收货人',
  `phone` VARCHAR(20) DEFAULT '' COMMENT '联系电话',
  `address_region` VARCHAR(64) DEFAULT '' COMMENT '省市区',
  `address_detail` VARCHAR(256) DEFAULT '' COMMENT '详细地址',
  `express_company` VARCHAR(32) DEFAULT '' COMMENT '物流公司',
  `express_number` VARCHAR(64) DEFAULT '' COMMENT '物流单号',
  `evaluated` TINYINT DEFAULT 0 COMMENT '是否已评价',
  `remark` VARCHAR(256) DEFAULT '' COMMENT '订单备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `paid_at` DATETIME DEFAULT NULL COMMENT '支付时间',
  `shipped_at` DATETIME DEFAULT NULL COMMENT '发货时间',
  `received_at` DATETIME DEFAULT NULL COMMENT '收货时间',
  INDEX `idx_user` (`user_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_order_no` (`order_no`),
  INDEX `idx_user_status` (`user_id`, `status`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- 订单商品表
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(128) DEFAULT '' COMMENT '商品名称',
  `product_image` VARCHAR(512) DEFAULT '' COMMENT '商品图片',
  `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `quantity` INT DEFAULT 1 COMMENT '数量',
  `spec` VARCHAR(64) DEFAULT '' COMMENT '所选规格',
  INDEX `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- ----------------------------
-- 打卡记录表（类似小红书笔记）
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '笔记ID',
  `user_id` BIGINT NOT NULL COMMENT '发布者用户ID',
  `heritage_id` BIGINT DEFAULT NULL COMMENT '关联非遗ID',
  `heritage_name` VARCHAR(128) DEFAULT '' COMMENT '非遗名称（冗余）',
  `content` TEXT COMMENT '文字内容',
  `title` VARCHAR(128) DEFAULT '' COMMENT '打卡标题',
  `images` TEXT COMMENT '图片URL列表JSON',
  `location_name` VARCHAR(128) DEFAULT '' COMMENT '位置名称',
  `latitude` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
  `longitude` DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
  `tags` VARCHAR(256) DEFAULT '' COMMENT '标签',
  `topic` VARCHAR(64) DEFAULT '' COMMENT '所属话题',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT DEFAULT 0 COMMENT '评论数',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1正常 0删除',
  `visibility` VARCHAR(16) DEFAULT 'public' COMMENT '可见性 public/private',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_user` (`user_id`),
  INDEX `idx_heritage` (`heritage_id`),
  INDEX `idx_topic` (`topic`),
  INDEX `idx_created` (`created_at`),
  INDEX `idx_vis_status` (`visibility`, `status`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡笔记表';

-- ----------------------------
-- 评论表
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  `post_id` BIGINT NOT NULL COMMENT '关联笔记ID',
  `user_id` BIGINT NOT NULL COMMENT '评论者用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `reply_to_user_id` BIGINT DEFAULT NULL COMMENT '回复目标用户ID',
  `reply_to_content` VARCHAR(512) DEFAULT '' COMMENT '回复的原文',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_post` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ----------------------------
-- 点赞表
-- ----------------------------
DROP TABLE IF EXISTS `likes`;
CREATE TABLE `likes` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
  `post_id` BIGINT NOT NULL COMMENT '笔记ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  INDEX `idx_post` (`post_id`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- ----------------------------
-- 关注表
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关注ID',
  `follower_id` BIGINT NOT NULL COMMENT '关注者',
  `following_id` BIGINT NOT NULL COMMENT '被关注者',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_follow` (`follower_id`, `following_id`),
  INDEX `idx_follower` (`follower_id`),
  INDEX `idx_following` (`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注表';

-- ----------------------------
-- 收藏表
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `target_id` BIGINT NOT NULL COMMENT '目标ID（非遗或商品）',
  `target_type` VARCHAR(16) NOT NULL COMMENT '类型 heritage/product',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_fav` (`user_id`, `target_id`, `target_type`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ----------------------------
-- 探访路线表
-- ----------------------------
DROP TABLE IF EXISTS `route`;
CREATE TABLE `route` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '路线ID',
  `title` VARCHAR(128) NOT NULL COMMENT '路线名称',
  `description` TEXT COMMENT '路线介绍',
  `cover_image` VARCHAR(512) DEFAULT '' COMMENT '封面图',
  `duration` VARCHAR(32) DEFAULT '' COMMENT '建议时长（如一天）',
  `city` VARCHAR(32) DEFAULT '' COMMENT '所属城市',
  `tags` VARCHAR(256) DEFAULT '' COMMENT '标签',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_city` (`city`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='探访路线表';

-- ----------------------------
-- 路线节点表
-- ----------------------------
DROP TABLE IF EXISTS `route_point`;
CREATE TABLE `route_point` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `route_id` BIGINT NOT NULL COMMENT '路线ID',
  `heritage_id` BIGINT NOT NULL COMMENT '非遗ID',
  `point_order` INT DEFAULT 0 COMMENT '顺序',
  `stay_time` VARCHAR(32) DEFAULT '' COMMENT '建议停留时间',
  `note` VARCHAR(256) DEFAULT '' COMMENT '小贴士',
  INDEX `idx_route` (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路线节点表';

-- ----------------------------
-- 收货地址表
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `consignee` VARCHAR(32) NOT NULL COMMENT '收件人',
  `phone` VARCHAR(20) NOT NULL COMMENT '电话',
  `region` VARCHAR(64) DEFAULT '' COMMENT '省市区',
  `detail` VARCHAR(256) DEFAULT '' COMMENT '详细地址',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ----------------------------
-- 购物车表
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `quantity` INT DEFAULT 1 COMMENT '数量',
  `spec` VARCHAR(64) DEFAULT '' COMMENT '规格',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_cart` (`user_id`, `product_id`, `spec`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================
-- 插入默认数据
-- ============================================

-- 默认管理员用户（openid占位）
INSERT INTO `user` (`account`, `openid`, `nick_name`, `balance`) VALUES
('admin', 'system_admin', '江右拾遗', 0);

-- 插入路线数据
INSERT INTO `route` (`title`, `description`, `duration`, `city`) VALUES
('景德镇陶瓷一日游', '古窑民俗博览区 → 中国陶瓷博物馆 → 陶溪川文创街区', '一天', '景德镇市'),
('婺源非遗两日游', '第一天：傩舞表演 → 徽剧欣赏；第二天：绿茶制作体验 → 古村游览', '两天', '上饶市'),
('赣南客家文化之旅', '赣南采茶戏 → 客家擂茶体验 → 客家围屋参观', '两天', '赣州市');

SELECT '✅ 数据库初始化完成！' AS result;