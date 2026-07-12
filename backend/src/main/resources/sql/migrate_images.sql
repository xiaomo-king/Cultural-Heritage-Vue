-- ============================================
-- 图片路径迁移脚本（带子目录版本）
-- 将所有旧图片路径改为带子目录的新路径
-- 运行前需先确保 user/images/ 的图片已移到 backend/uploads/images/ 对应子目录
-- ============================================

USE jiangyou_shiyi;

-- 1. 更新 heritage 表
UPDATE `heritage`
SET `cover_image` = REPLACE(`cover_image`, '/images/heritage/', '/uploads/images/heritage/'),
    `images` = REPLACE(`images`, '/images/heritage/', '/uploads/images/heritage/')
WHERE `cover_image` LIKE '/images/heritage/%' OR `images` LIKE '/images/heritage/%';

-- 2. 更新 product 表
UPDATE `product`
SET `images` = REPLACE(`images`, '/images/products/', '/uploads/images/products/')
WHERE `images` LIKE '/images/products/%';

-- 3. 更新 post 表（打卡图片，使用的是 heritage 目录下的图）
UPDATE `post`
SET `images` = REPLACE(`images`, '/images/heritage/', '/uploads/images/heritage/')
WHERE `images` LIKE '/images/heritage/%';

-- 4. 更新 order_item 表（商品图片）
UPDATE `order_item`
SET `product_image` = REPLACE(`product_image`, '/images/products/', '/uploads/images/products/')
WHERE `product_image` LIKE '/images/products/%';

-- 查看受影响的行数
SELECT 'heritage' AS `表名`, COUNT(*) AS `更新行数` FROM `heritage` WHERE `cover_image` LIKE '/uploads/images/heritage/%';
SELECT 'product' AS `表名`, COUNT(*) AS `更新行数` FROM `product` WHERE `images` LIKE '/uploads/images/products/%';
SELECT 'post' AS `表名`, COUNT(*) AS `更新行数` FROM `post` WHERE `images` LIKE '/uploads/images/heritage/%';
SELECT 'order_item' AS `表名`, COUNT(*) AS `更新行数` FROM `order_item` WHERE `product_image` LIKE '/uploads/images/products/%';
