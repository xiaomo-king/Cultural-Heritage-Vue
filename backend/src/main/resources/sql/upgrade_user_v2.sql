-- ============================================
-- 江右拾遗 - 用户表升级：账号/昵称分离
-- ============================================
USE jiangyou_shiyi;

-- 清空旧数据重建
DELETE FROM `likes`;
DELETE FROM `follow`;
DELETE FROM `favorite`;
DELETE FROM `comment`;
DELETE FROM `post`;
DELETE FROM `user`;

ALTER TABLE `user`
  ADD COLUMN `account` VARCHAR(64) NOT NULL UNIQUE COMMENT '登录账号' AFTER `id`,
  MODIFY `nick_name` VARCHAR(64) DEFAULT '' COMMENT '昵称',
  ADD COLUMN `gender` VARCHAR(8) DEFAULT '' COMMENT '性别' AFTER `avatar_url`,
  ADD COLUMN `bio` VARCHAR(256) DEFAULT '' COMMENT '简介' AFTER `gender`,
  ADD COLUMN `location` VARCHAR(128) DEFAULT '' COMMENT '所在地' AFTER `bio`;

ALTER TABLE `user` AUTO_INCREMENT = 1;

SELECT '✅ 升级完成' AS result;
