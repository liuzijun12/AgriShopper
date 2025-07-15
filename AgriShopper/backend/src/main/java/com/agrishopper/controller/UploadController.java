package com.agrishopper.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Value("${file.upload.path:static/uploads/}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:/static/uploads/}")
    private String urlPrefix;

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始处理文件上传: {}", file.getOriginalFilename());
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                logger.warn("文件类型不支持: {}", contentType);
                response.put("code", 400);
                response.put("message", "只能上传图片文件");
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }

            // 检查文件大小（限制为5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                logger.warn("文件大小超限: {} bytes", file.getSize());
                response.put("code", 400);
                response.put("message", "图片大小不能超过5MB");
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }

            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
            logger.info("上传目录: {}", uploadDir);
            
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                logger.info("创建上传目录: {}", uploadDir);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            logger.info("生成文件名: {}", filename);

            // 保存文件
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            logger.info("文件保存成功: {}", filePath);

            // 返回文件URL
            String fileUrl = urlPrefix + filename;
            logger.info("文件访问URL: {}", fileUrl);
            
            // 为了兼容前端Element Plus的上传组件，直接返回URL字符串
            response.put("code", 200);
            response.put("message", "上传成功");
            response.put("data", fileUrl);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            logger.error("文件上传失败", e);
            response.put("code", 500);
            response.put("message", "文件上传失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.internalServerError().body(response);
        } catch (Exception e) {
            logger.error("文件上传过程中发生未知错误", e);
            response.put("code", 500);
            response.put("message", "文件上传失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        // 通用文件上传接口，调用图片上传逻辑
        return uploadImage(file);
    }
} 