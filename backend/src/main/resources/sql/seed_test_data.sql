-- ============================================
-- 江右拾遗 - 全功能测试数据脚本
-- 在 Navicat 中可反复安全运行（先清后插）
-- 覆盖：用户、打卡、评论、点赞、关注、地址、订单、购物车、收藏
-- ============================================

USE jiangyou_shiyi;

-- ============================================
-- 清空旧数据（先删子表，再删父表）
-- ============================================
DELETE FROM `order_item`;
DELETE FROM `orders`;
DELETE FROM `address`;
DELETE FROM `cart`;
DELETE FROM `favorite`;
DELETE FROM `follow`;
DELETE FROM `likes`;
DELETE FROM `comment`;
DELETE FROM `post`;
DELETE FROM `user` WHERE `id` > 1;

-- 重置自增ID
ALTER TABLE `post` AUTO_INCREMENT = 1;
ALTER TABLE `comment` AUTO_INCREMENT = 1;
ALTER TABLE `likes` AUTO_INCREMENT = 1;
ALTER TABLE `follow` AUTO_INCREMENT = 1;
ALTER TABLE `favorite` AUTO_INCREMENT = 1;
ALTER TABLE `address` AUTO_INCREMENT = 1;
ALTER TABLE `orders` AUTO_INCREMENT = 1;
ALTER TABLE `order_item` AUTO_INCREMENT = 1;
ALTER TABLE `cart` AUTO_INCREMENT = 1;

-- ============================================
-- 1. 注册两个测试用户
-- ============================================
INSERT INTO `user` (`id`, `account`, `openid`, `password`, `nick_name`, `avatar_url`, `gender`, `bio`, `location`, `phone`, `balance`, `join_date`, `last_login`, `follow_count`, `follower_count`, `checkin_count`, `status`) VALUES
(2, 'zhangsan', 'pwd_zhangsan01', '123456', '张三', '', '男', '景德镇陶瓷爱好者，走遍江西非遗点', '景德镇市', '13812345678', 500.00, '2026-06-01 10:00:00', '2026-07-10 08:00:00', 0, 0, 0, 1),
(3, 'lisi1111', 'pwd_lisi0011', '123456', '李四', '', '女', '南昌姑娘，想把家乡非遗拍给更多人看', '南昌市', '15987654321', 300.00, '2026-06-05 14:30:00', '2026-07-09 20:00:00', 0, 0, 0, 1);

-- ============================================
-- 2. 打卡笔记（6条）
-- ============================================
INSERT INTO `post` (`id`, `user_id`, `heritage_id`, `heritage_name`, `title`, `content`, `images`, `location_name`, `tags`, `topic`, `like_count`, `comment_count`, `status`, `visibility`, `created_at`) VALUES
(1, 2, 1, '景德镇手工制瓷技艺',
 '在古窑邂逅青花之美',
 '趁着周末来景德镇古窑民俗博览区，亲眼看到了非遗传承人现场拉坯。那种泥巴在手中旋转成型的感觉太神奇了！最后的青花茶杯成品，白如玉、明如镜，真的名不虚传。推荐大家来景德镇一定要体验一次手工制瓷。',
 '/uploads/images/posts/景德镇手工制瓷技艺.jpg,/uploads/images/posts/赣南采茶戏.jpg',
 '景德镇古窑民俗博览区', '景德镇,陶瓷,拉坯体验,周末游',
 '景德镇逛窑指南', 0, 0, 1, 'public', '2026-06-10 09:30:00'),

(2, 2, 3, '南丰跳傩',
 '傩舞之乡的震撼体验',
 '南丰跳傩真的太震撼了！第一次看到这种古老的面具舞蹈，舞者戴着各式傩面具，动作刚劲有力。据说这是古代驱鬼逐疫的仪式，现在成了珍贵的非遗表演。每一副面具都是手工雕刻的，神态各异。',
 '/uploads/images/posts/赣剧.jpg',
 '抚州市南丰县', '南丰,傩舞,面具,民俗',
 '傩舞之乡', 0, 0, 1, 'public', '2026-06-15 14:00:00'),

(3, 2, 9, '铅山连四纸制作技艺',
 '探访即将失传的连四纸',
 '专门跑到铅山去看连四纸的制作。老师傅说全套工序72道，从选竹到成品要一年多时间。这种纸连皇帝都用它来写圣旨，真的薄如蝉翼、洁白如玉。可惜现在会做的人越来越少了，希望非遗保护能让它传承下去。',
 '/uploads/images/posts/吉州窑陶瓷烧制技艺.jpg',
 '上饶市铅山县', '铅山,连四纸,古法造纸,手艺',
 '江西非遗美食地图', 0, 0, 1, 'public', '2026-06-20 11:00:00'),

(4, 3, 4, '婺源傩舞',
 '婺源傩舞——在徽派老宅前看傩',
 '在婺源的古村里看傩舞表演，背景是白墙黛瓦的徽派建筑，画面美得像画一样。婺源傩舞比南丰的多了些柔美，动作更加舒展。表演结束后还和傩舞传承人聊了会儿天，他说现在来学傩舞的年轻人多了，是个好现象。',
 '/uploads/images/posts/瓷板画.jpg',
 '上饶市婺源县', '婺源,傩舞,古村,徽派建筑',
 '傩舞之乡', 0, 0, 1, 'public', '2026-06-22 16:00:00'),

(5, 3, 16, '全丰花灯',
 '修水全丰花灯——灯火里的非遗',
 '元宵节去修水看全丰花灯，真的太美了！鲤鱼灯、荷花灯、龙凤灯，各式各样的花灯把整个小镇点亮了。每盏花灯都是手工制作的，竹编骨架、丝绸裱糊，色彩艳丽又不失雅致。买了一盏鲤鱼灯回来做纪念。',
 '/uploads/images/posts/解缙故事.jpg',
 '九江市修水县', '全丰花灯,修水,元宵,民俗灯会',
 '江西非遗美食地图', 0, 0, 1, 'public', '2026-06-25 19:30:00'),

(6, 3, 7, '赣南采茶戏',
 '赣南采茶戏——客家山歌里的故事',
 '在赣州看了一场地道的赣南采茶戏。演员们载歌载舞，用客家话唱出生动的故事。最有意思的是"矮子步"和"扇子花"，俏皮又灵动。虽然是第一次看，但完全被这种充满生活气息的表演感染了。',
 '/uploads/images/posts/赣南采茶戏.jpg',
 '赣州市', '赣南采茶戏,客家文化,山歌,戏曲',
 '赣南客家风情', 0, 0, 1, 'public', '2026-07-01 15:00:00');

-- ============================================
-- 3. 评论（8条）
-- ============================================
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `created_at`) VALUES
(1, 3, '哇，青花瓷好漂亮！请问古窑的门票多少钱呀？', '2026-06-10 10:00:00'),
(1, 2, '有学生证的话半价，全票好像是85元', '2026-06-10 10:30:00'),
(2, 3, '南丰跳傩一直想去看，请问是在哪个村？', '2026-06-15 15:00:00'),
(4, 2, '婺源太美了！求问是哪个古村？', '2026-06-22 20:00:00'),
(4, 3, '在汪口村看的，下午三点有表演', '2026-06-22 21:00:00'),
(5, 2, '花灯好美！这个要特定时间去才能看到吗？', '2026-06-25 20:00:00'),
(3, 3, '连四纸真的快失传了，好可惜', '2026-06-20 14:00:00'),
(6, 2, '赣南采茶戏小时候经常看，满满的回忆', '2026-07-01 16:00:00');

-- ============================================
-- 4. 点赞（每人每篇最多1次）
-- ============================================
INSERT INTO `likes` (`post_id`, `user_id`, `created_at`) VALUES
(1, 3, '2026-06-10 10:00:00'),
(2, 3, '2026-06-15 15:30:00'),
(3, 3, '2026-06-20 12:00:00'),
(4, 2, '2026-06-22 20:00:00'),
(5, 2, '2026-06-25 20:30:00'),
(6, 2, '2026-07-01 16:30:00');

-- ============================================
-- 5. 关注
-- ============================================
INSERT INTO `follow` (`follower_id`, `following_id`, `created_at`) VALUES
(2, 3, '2026-06-10 10:00:00'),
(3, 2, '2026-06-10 11:00:00');

-- ============================================
-- 6. 收藏
-- ============================================
INSERT INTO `favorite` (`user_id`, `target_id`, `target_type`, `created_at`) VALUES
(2, 1, 'heritage', '2026-06-10 09:00:00'),
(2, 3, 'heritage', '2026-06-15 13:00:00'),
(3, 4, 'heritage', '2026-06-22 15:00:00'),
(3, 1, 'product', '2026-06-12 10:00:00'),
(2, 5, 'product', '2026-06-20 14:00:00');

-- ============================================
-- 7. 收货地址
-- ============================================
INSERT INTO `address` (`user_id`, `consignee`, `phone`, `region`, `detail`, `is_default`, `created_at`) VALUES
(2, '张三', '13800138001', '江西省 景德镇市 昌江区', '古窑路18号 景德镇古窑瓷庄', 1, '2026-06-01 10:00:00'),
(2, '张先生', '13800138002', '江西省 南昌市 红谷滩区', '万达广场A座1203', 0, '2026-06-10 10:00:00'),
(3, '李四', '13900139001', '江西省 南昌市 青山湖区', '北京东路1666号', 1, '2026-06-05 14:00:00');

-- ============================================
-- 8. 订单（4笔）
-- ============================================

-- 订单1：张三 - 待发货
INSERT INTO `orders` (`order_no`, `user_id`, `total_amount`, `status`, `consignee`, `phone`, `address_region`, `address_detail`, `remark`, `created_at`, `paid_at`) VALUES
('JY20260708001', 2, 298.00, 'paid', '张三', '13800138001', '江西省 景德镇市 昌江区', '古窑路18号 景德镇古窑瓷庄', '麻烦包装好一点，送人的', '2026-07-08 10:00:00', '2026-07-08 10:05:00');
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `spec`) VALUES
(1, 1, '青花瓷茶杯', '/uploads/images/products/青花瓷茶杯.jpg', 298.00, 1, '对杯（含礼盒）');

-- 订单2：张三 - 已发货
INSERT INTO `orders` (`order_no`, `user_id`, `total_amount`, `status`, `consignee`, `phone`, `address_region`, `address_detail`, `express_company`, `express_number`, `remark`, `created_at`, `paid_at`, `shipped_at`) VALUES
('JY20260701002', 2, 788.00, 'shipped', '张先生', '13800138002', '江西省 南昌市 红谷滩区', '万达广场A座1203', '中通快递', 'ZT20260701001', '', '2026-07-01 14:00:00', '2026-07-01 14:05:00', '2026-07-02 09:00:00');
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `spec`) VALUES
(2, 6, '瓷板画挂饰', '/uploads/images/products/瓷板画挂饰.jpg', 788.00, 1, '扇形（30×40cm）');

-- 订单3：李四 - 已收货
INSERT INTO `orders` (`order_no`, `user_id`, `total_amount`, `status`, `consignee`, `phone`, `address_region`, `address_detail`, `express_company`, `express_number`, `remark`, `created_at`, `paid_at`, `shipped_at`, `received_at`) VALUES
('JY20260620003', 3, 228.00, 'received', '李四', '13900139001', '江西省 南昌市 青山湖区', '北京东路1666号', '圆通快递', 'YT20260620001', '请放快递柜', '2026-06-20 10:00:00', '2026-06-20 10:03:00', '2026-06-21 14:00:00', '2026-06-23 09:00:00');
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `spec`) VALUES
(3, 4, '婺源绿茶礼盒', '/uploads/images/products/婺源绿茶礼盒.jpg', 228.00, 1, '500g精品礼盒');

-- 订单4：李四 - 待发货（2件商品）
INSERT INTO `orders` (`order_no`, `user_id`, `total_amount`, `status`, `consignee`, `phone`, `address_region`, `address_detail`, `remark`, `created_at`, `paid_at`) VALUES
('JY20260705004', 3, 296.00, 'paid', '李四', '13900139001', '江西省 南昌市 青山湖区', '北京东路1666号', '', '2026-07-05 16:00:00', '2026-07-05 16:02:00');
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `spec`) VALUES
(4, 8, '瑞昌剪纸摆件', '/uploads/images/products/瑞昌剪纸摆件.jpg', 128.00, 1, '中号（30×30cm）'),
(4, 9, '傩面具挂件', '/uploads/images/products/傩面具挂件.jpg', 128.00, 1, '开山（驱邪）');

-- ============================================
-- 9. 购物车
-- ============================================
INSERT INTO `cart` (`user_id`, `product_id`, `quantity`, `spec`, `created_at`) VALUES
(2, 3, 1, '标准盏（口径8cm）', '2026-07-09 10:00:00'),
(2, 5, 1, '小号（30×40cm）', '2026-07-09 11:00:00'),
(3, 7, 2, '素色信笺（50张）', '2026-07-08 14:00:00');

-- ============================================
-- 10. 更新统计字段
-- ============================================
UPDATE `user` SET
  `follow_count` = (SELECT COUNT(*) FROM `follow` WHERE `follower_id` = `user`.`id`),
  `follower_count` = (SELECT COUNT(*) FROM `follow` WHERE `following_id` = `user`.`id`),
  `checkin_count` = (SELECT COUNT(*) FROM `post` WHERE `user_id` = `user`.`id`)
WHERE `id` IN (2, 3);

UPDATE `heritage` h
SET `checkin_count` = (SELECT COUNT(*) FROM `post` WHERE `heritage_id` = h.`id` AND `status` = 1);


-- ============================================
-- 12. 同步打卡的点赞数和评论数（与 likes/comment 表保持一致）
-- ============================================
UPDATE `post` p
SET p.`like_count` = (SELECT COUNT(*) FROM `likes` WHERE `post_id` = p.`id`),
    p.`comment_count` = (SELECT COUNT(*) FROM `comment` WHERE `post_id` = p.`id`);

-- ============================================
-- 13. 汇总确认
-- ============================================
-- 11. 汇总确认
-- ============================================
SELECT '✅ 测试数据已全部导入！' AS result;
SELECT CONCAT('用户数: ', (SELECT COUNT(*) FROM `user`)) AS `统计`;
SELECT CONCAT('打卡数: ', (SELECT COUNT(*) FROM `post`)) AS `统计`;
SELECT CONCAT('评论数: ', (SELECT COUNT(*) FROM `comment`)) AS `统计`;
SELECT CONCAT('点赞数: ', (SELECT COUNT(*) FROM `likes`)) AS `统计`;
SELECT CONCAT('订单数: ', (SELECT COUNT(*) FROM `orders`)) AS `统计`;
SELECT CONCAT('地址数: ', (SELECT COUNT(*) FROM `address`)) AS `统计`;
