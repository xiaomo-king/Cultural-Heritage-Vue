package com.jiangyou.controller;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    private final FileUploadService fileUploadService;
    public UploadController(FileUploadService fus) { this.fileUploadService = fus; }

    /**
     * 上传单张图片
     * @param file 图片文件
     * @param type 分类：heritage / product / post / avatar，默认 heritage
     */
    @PostMapping("/image")
    public ApiResponse<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "heritage") String type) {
        if (file.isEmpty()) {
            return ApiResponse.error("文件不能为空");
        }
        String url = fileUploadService.upload(file, type);
        log.info("图片上传成功: type={}, url={}, size={}, original={}", type, url, file.getSize(), file.getOriginalFilename());
        return ApiResponse.success(Map.of("url", url));
    }

    /**
     * 批量上传图片
     * @param files 多张图片
     * @param type 分类，默认 heritage
     */
    @PostMapping("/images")
    public ApiResponse<List<String>> uploadImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(required = false, defaultValue = "heritage") String type) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile f : files) {
            if (!f.isEmpty()) {
                urls.add(fileUploadService.upload(f, type));
            }
        }
        return ApiResponse.success(urls);
    }
}
