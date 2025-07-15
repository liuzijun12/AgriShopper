package com.agrishopper.utils;

import java.util.Random;

/**
 * 商品编码生成工具类
 */
public class ProductCodeGenerator {
    
    private static final Random random = new Random();
    
    /**
     * 生成6位随机数字商品编码
     * @return 6位数字字符串
     */
    public static String generateProductCode() {
        // 生成100000到999999之间的随机数
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    
    /**
     * 生成指定长度的随机数字编码
     * @param length 编码长度
     * @return 随机数字字符串
     */
    public static String generateProductCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("编码长度必须大于0");
        }
        
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    /**
     * 生成带前缀的商品编码
     * @param prefix 前缀（如：P）
     * @param length 数字部分长度
     * @return 带前缀的商品编码
     */
    public static String generateProductCodeWithPrefix(String prefix, int length) {
        return prefix + generateProductCode(length);
    }
} 