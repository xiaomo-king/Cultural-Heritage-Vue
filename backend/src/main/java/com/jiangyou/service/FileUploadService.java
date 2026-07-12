package com.jiangyou.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    private String absoluteUploadPath;

    // 所有支持的图片子目录
    private static final String[] SUB_DIRS = {"heritage", "products", "posts", "avatars"};

    @PostConstruct
    public void init() {
        Path path = Paths.get(uploadPath).toAbsolutePath().normalize();
        this.absoluteUploadPath = path.toString();

        // 确保 images 目录及所有子目录存在
        File imagesDir = new File(absoluteUploadPath + "/images");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }
        for (String sub : SUB_DIRS) {
            File dir = new File(absoluteUploadPath + "/images/" + sub);
            if (!dir.exists()) {
                dir.mkdirs();
                log.info("创建图片子目录: {}", dir.getAbsolutePath());
            }
        }
        System.out.println("✅ 文件上传目录: " + imagesDir.getAbsolutePath());
    }

    /**
     * 上传图片到指定分类子目录
     * @param file 上传文件
     * @param type 分类：heritage / product / post / avatar
     * @return 图片相对路径，如 /uploads/images/heritage/xxx.jpg
     */
    public String upload(MultipartFile file, String type) {
        try {
            // 校验 type 合法性
            if (type == null || type.isEmpty()) {
                type = "heritage";
            }
            String dir = absoluteUploadPath + "/images/" + type;
            new File(dir).mkdirs();

            String ext = file.getOriginalFilename();
            ext = ext != null && ext.contains(".") ? ext.substring(ext.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;
            File dest = new File(dir, filename);
            file.transferTo(dest);
            log.info("文件上传成功: {} -> {}", file.getOriginalFilename(), dest.getAbsolutePath());
            return "/uploads/images/" + type + "/" + filename;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
}
