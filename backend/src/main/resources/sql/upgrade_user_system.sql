-- ============================================
-- 江右拾遗 - 用户系统升级脚本
-- 1. user表：加password字段，nick_name改为唯一
-- 2. post表：加visibility字段
-- ============================================
USE jiangyou_shiyi;

-- 清空旧用户数据（自动登录的残留）
DELETE FROM `likes`;
DELETE FROM `follow`;
DELETE FROM `favorite`;
DELETE FROM `comment`;
DELETE FROM `post`;
DELETE FROM `user`;

-- user表：加password字段，nick_name加唯一索引
ALTER TABLE `user`
  MODIFY `openid` VARCHAR(64) NULL,
  ADD COLUMN `password` VARCHAR(128) DEFAULT '' COMMENT '密码' AFTER `openid`,
  ADD UNIQUE INDEX `idx_nick_name` (`nick_name`);

-- post表加可见性字段
ALTER TABLE `post`
  ADD COLUMN `visibility` VARCHAR(16) DEFAULT 'public' COMMENT '可见性 public/private' AFTER `status`;

SELECT '✅ 升级完成！' AS result;
