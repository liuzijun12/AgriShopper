package com.agrishopper.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:static/uploads/}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /static/uploads/** 映射到实际的文件目录
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        String uploadLocation = "file:" + uploadDir.toString() + "/";
        
        registry.addResourceHandler("/static/uploads/**")
                .addResourceLocations(uploadLocation)
                .setCachePeriod(3600) // 缓存1小时
                .resourceChain(true);
        
        // 添加日志输出以便调试
        System.out.println("Static resource mapping: /static/uploads/** -> " + uploadLocation);
        System.out.println("Upload directory exists: " + uploadDir.toFile().exists());
        System.out.println("Upload directory path: " + uploadDir.toString());
    }
} 