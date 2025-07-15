package com.agrishopper.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;

/**
 * 微信数据解密工具类
 */
@Component
public class WxDataDecryptUtil {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 解密微信用户数据
     * @param encryptedData 加密的用户数据
     * @param iv 加密算法的初始向量
     * @param sessionKey 会话密钥
     * @return 解密后的用户信息
     */
    public Map<String, Object> decryptUserData(String encryptedData, String iv, String sessionKey) {
        try {
            System.out.println("=== 开始解密微信用户数据 ===");
            System.out.println("encryptedData长度: " + (encryptedData != null ? encryptedData.length() : 0));
            System.out.println("iv长度: " + (iv != null ? iv.length() : 0));
            System.out.println("sessionKey长度: " + (sessionKey != null ? sessionKey.length() : 0));
            
            // 参数验证
            if (encryptedData == null || encryptedData.trim().isEmpty()) {
                throw new RuntimeException("encryptedData不能为空");
            }
            if (iv == null || iv.trim().isEmpty()) {
                throw new RuntimeException("iv不能为空");
            }
            if (sessionKey == null || sessionKey.trim().isEmpty()) {
                throw new RuntimeException("sessionKey不能为空");
            }
            
            // Base64解码
            byte[] dataByte = Base64.decodeBase64(encryptedData);
            byte[] keyByte = Base64.decodeBase64(sessionKey);
            byte[] ivByte = Base64.decodeBase64(iv);
            
            System.out.println("解码后 - dataByte长度: " + dataByte.length);
            System.out.println("解码后 - keyByte长度: " + keyByte.length);
            System.out.println("解码后 - ivByte长度: " + ivByte.length);
            
            // 验证密钥长度（AES-128需要16字节）
            if (keyByte.length != 16) {
                throw new RuntimeException("sessionKey长度不正确，期望16字节，实际" + keyByte.length + "字节");
            }
            if (ivByte.length != 16) {
                throw new RuntimeException("iv长度不正确，期望16字节，实际" + ivByte.length + "字节");
            }
            
            // 对称解密使用的算法为 AES-128-CBC，数据采用PKCS#7填充
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivByte);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            
            // 解密
            byte[] decrypted = cipher.doFinal(dataByte);
            String result = new String(decrypted, StandardCharsets.UTF_8);
            
            System.out.println("解密结果: " + result);
            
            // 解析JSON
            Map<String, Object> userInfo = objectMapper.readValue(result, Map.class);
            System.out.println("=== 解密成功，解析后的用户信息 ===");
            System.out.println("昵称: " + userInfo.get("nickName"));
            System.out.println("性别: " + userInfo.get("gender"));
            System.out.println("国家: " + userInfo.get("country"));
            System.out.println("省份: " + userInfo.get("province"));
            System.out.println("城市: " + userInfo.get("city"));
            System.out.println("头像: " + userInfo.get("avatarUrl"));
            
            return userInfo;
            
        } catch (Exception e) {
            System.out.println("=== 解密失败 ===");
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("解密微信用户数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证数据签名
     * @param rawData 原始数据
     * @param signature 签名
     * @param sessionKey 会话密钥
     * @return 验证结果
     */
    public boolean verifySignature(String rawData, String signature, String sessionKey) {
        try {
            System.out.println("开始验证数据签名...");
            
            // 构造签名字符串
            String signStr = rawData + sessionKey;
            System.out.println("签名字符串: " + signStr);
            
            // 计算SHA1
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(signStr.getBytes(StandardCharsets.UTF_8));
            
            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            String calculatedSignature = hexString.toString();
            System.out.println("计算的签名: " + calculatedSignature);
            System.out.println("接收的签名: " + signature);
            
            boolean isValid = calculatedSignature.equals(signature);
            System.out.println("签名验证结果: " + isValid);
            
            return isValid;
            
        } catch (Exception e) {
            System.out.println("签名验证失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 