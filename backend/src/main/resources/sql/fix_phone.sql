-- ============================================
-- 补充测试用户的手机号
-- 在 Navicat 中运行
-- ============================================
USE jiangyou_shiyi;

UPDATE `user` SET `phone` = '13812345678' WHERE `account` = 'zhangsan';
UPDATE `user` SET `phone` = '15987654321' WHERE `account` = 'lisi1111';

SELECT `account`, `nick_name`, `phone` FROM `user` WHERE `account` IN ('zhangsan', 'lisi1111');
