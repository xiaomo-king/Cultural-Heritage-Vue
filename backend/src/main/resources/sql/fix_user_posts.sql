USE jiangyou_shiyi;

-- 查看当前用户ID
SELECT id, account, nick_name FROM `user`;

-- 修复打卡的 user_id
-- post 1-3 应该是张三（id=3），post 4-6 应该是李四（id=2）
UPDATE `post` SET `user_id` = 3 WHERE `id` IN (1, 2, 3);
UPDATE `post` SET `user_id` = 2 WHERE `id` IN (4, 5, 6);

-- 同步用户统计
UPDATE `user` SET `checkin_count` = (SELECT COUNT(*) FROM `post` WHERE `user_id` = `user`.`id`);

-- 验证结果
SELECT p.id, p.user_id, u.nick_name
FROM `post` p
LEFT JOIN `user` u ON p.user_id = u.id
ORDER BY p.id;
