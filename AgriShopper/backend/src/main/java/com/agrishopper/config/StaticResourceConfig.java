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
        
        // 映射 /static/icon/** 到 classpath:static/icon/
        registry.addResourceHandler("/static/icon/**")
                .addResourceLocations("classpath:static/icon/")
                .setCachePeriod(3600) // 缓存1小时
                .resourceChain(true);
        
        // 映射 /static/messages/** 到 classpath:static/messages/
        registry.addResourceHandler("/static/messages/**")
                .addResourceLocations("classpath:static/messages/")
                .setCachePeriod(3600) // 缓存1小时
                .resourceChain(true);
        
        // 映射 /static/Carousel/** 到 classpath:static/Carousel/
        registry.addResourceHandler("/static/Carousel/**")
                .addResourceLocations("classpath:static/Carousel/")
                .setCachePeriod(3600) // 缓存1小时
                .resourceChain(true);
        
        // 添加日志输出以便调试
        System.out.println("Static resource mapping: /static/uploads/** -> " + uploadLocation);
        System.out.println("Static resource mapping: /static/icon/** -> classpath:static/icon/");
        System.out.println("Static resource mapping: /static/messages/** -> classpath:static/messages/");
        System.out.println("Static resource mapping: /static/Carousel/** -> classpath:static/Carousel/");
        System.out.println("Upload directory exists: " + uploadDir.toFile().exists());
        System.out.println("Upload directory path: " + uploadDir.toString());
    }
} 