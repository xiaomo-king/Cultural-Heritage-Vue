-- ============================================
-- 修复打卡表的点赞数和评论数，使其与真实数据一致
-- 在 Navicat 中运行
-- ============================================
USE jiangyou_shiyi;

-- 更新打卡的点赞数 = likes 表中的真实计数
UPDATE `post` p
SET p.`like_count` = (SELECT COUNT(*) FROM `likes` WHERE `post_id` = p.`id`);

-- 更新打卡的评论数 = comment 表中的真实计数
UPDATE `post` p
SET p.`comment_count` = (SELECT COUNT(*) FROM `comment` WHERE `post_id` = p.`id`);

-- 查看修复结果
SELECT p.`id`, p.`title`, p.`like_count` AS `真实点赞数`, p.`comment_count` AS `真实评论数`
FROM `post` p
ORDER BY p.`id`;
