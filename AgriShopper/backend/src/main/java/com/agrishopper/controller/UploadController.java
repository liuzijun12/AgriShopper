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

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:/uploads/}")
    private String urlPrefix;

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("code", 400);
                response.put("message", "只能上传图片文件");
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }

            // 检查文件大小（限制为5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("code", 400);
                response.put("message", "图片大小不能超过5MB");
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }

            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 返回文件URL
            String fileUrl = urlPrefix + filename;
            
            Map<String, Object> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("filename", filename);
            data.put("originalName", originalFilename);
            data.put("size", file.getSize());

            response.put("code", 200);
            response.put("message", "上传成功");
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("code", 500);
            response.put("message", "文件上传失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 