package com.agrishopper.service.impl;

import com.agrishopper.model.WxUser;
import com.agrishopper.repository.WxUserRepository;
import com.agrishopper.service.WxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.agrishopper.utils.WxDataDecryptUtil;

/**
 * 微信用户服务实现类
 */
@Service
public class WxUserServiceImpl implements WxUserService {
    
    @Autowired
    private WxUserRepository wxUserRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private WxDataDecryptUtil wxDataDecryptUtil;
    
    @Value("${wx.appid}")
    private String appid;
    
    @Value("${wx.secret}")
    private String secret;
    
    @Value("${wx.login-url}")
    private String loginUrl;
    
    @Value("${wx.grant-type}")
    private String grantType;
    
    @Override
    public WxUser wxLogin(String code, WxUser userInfo) {
        return wxLogin(code, userInfo, null, null, null);
    }
    
    @Override
    public WxUser wxLogin(String code, WxUser userInfo, String encryptedData, String iv, String signature) {
        return wxLogin(code, userInfo, encryptedData, iv, signature, null);
    }
    
    @Override
    public WxUser wxLogin(String code, WxUser userInfo, String encryptedData, String iv, String signature, Map<String, Object> sessionInfo) {
        try {
            System.out.println("开始处理微信登录，code: " + code);
            
            // 1. 获取openid和session_key
            String openid = null;
            String sessionKey = null;
            String unionid = null;
            
            if (sessionInfo != null) {
                // 使用传入的session信息
                openid = (String) sessionInfo.get("openid");
                sessionKey = (String) sessionInfo.get("session_key");
                unionid = (String) sessionInfo.get("unionid");
                System.out.println("使用传入的session信息");
            } else {
                // 通过code获取session信息
                Map<String, String> wxResponse = getWxSession(code);
                openid = wxResponse.get("openid");
                sessionKey = wxResponse.get("session_key");
                unionid = wxResponse.get("unionid");
                System.out.println("通过code获取session信息");
            }
            
            if (openid == null) {
                throw new RuntimeException("获取微信用户信息失败");
            }
            
            System.out.println("获取到openid: " + openid);
            System.out.println("获取到unionid: " + unionid);
            System.out.println("获取到session_key: " + (sessionKey != null ? sessionKey.substring(0, 10) + "..." : "null"));
            
            // 2. 如果有加密数据，尝试解密获取完整用户信息
            WxUser decryptedUserInfo = null;
            if (encryptedData != null && iv != null && sessionKey != null) {
                try {
                    System.out.println("=== 开始解密用户数据 ===");
                    System.out.println("encryptedData: " + (encryptedData != null ? encryptedData.substring(0, Math.min(50, encryptedData.length())) + "..." : "null"));
                    System.out.println("iv: " + iv);
                    System.out.println("sessionKey: " + (sessionKey != null ? sessionKey.substring(0, Math.min(20, sessionKey.length())) + "..." : "null"));
                    
                    // 验证签名（如果有rawData）
                    if (signature != null && userInfo != null) {
                        // 这里需要前端传递rawData，暂时跳过签名验证
                        System.out.println("跳过签名验证");
                    }
                    
                    // 解密用户数据
                    Map<String, Object> decryptedData = wxDataDecryptUtil.decryptUserData(encryptedData, iv, sessionKey);
                    
                    if (decryptedData != null) {
                        decryptedUserInfo = new WxUser();
                        decryptedUserInfo.setNickname((String) decryptedData.get("nickName"));
                        decryptedUserInfo.setAvatar((String) decryptedData.get("avatarUrl"));
                        
                        Object genderObj = decryptedData.get("gender");
                        if (genderObj instanceof Integer) {
                            decryptedUserInfo.setGender((Integer) genderObj);
                        } else if (genderObj instanceof Number) {
                            decryptedUserInfo.setGender(((Number) genderObj).intValue());
                        }
                        
                        decryptedUserInfo.setCountry((String) decryptedData.get("country"));
                        decryptedUserInfo.setProvince((String) decryptedData.get("province"));
                        decryptedUserInfo.setCity((String) decryptedData.get("city"));
                        
                        System.out.println("=== 解密成功，用户信息已提取 ===");
                        System.out.println("解密后的昵称: " + decryptedUserInfo.getNickname());
                        System.out.println("解密后的性别: " + decryptedUserInfo.getGender());
                        System.out.println("解密后的地区: " + decryptedUserInfo.getCountry() + " " + decryptedUserInfo.getProvince() + " " + decryptedUserInfo.getCity());
                        System.out.println("解密后的头像: " + decryptedUserInfo.getAvatar());
                    }
                } catch (Exception e) {
                    System.out.println("=== 解密失败，将使用原始用户信息 ===");
                    System.out.println("解密错误: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("=== 缺少解密参数，跳过解密 ===");
                System.out.println("encryptedData: " + (encryptedData != null ? "有" : "无"));
                System.out.println("iv: " + (iv != null ? "有" : "无"));
                System.out.println("sessionKey: " + (sessionKey != null ? "有" : "无"));
            }
            
            // 3. 查找或创建用户
            Optional<WxUser> existingUser = wxUserRepository.findByOpenid(openid);
            WxUser wxUser;
            
            if (existingUser.isPresent()) {
                // 用户已存在，更新信息
                wxUser = existingUser.get();
                System.out.println("用户已存在，ID: " + wxUser.getId());
                
                // 优先使用解密后的信息，其次使用原始信息
                if (decryptedUserInfo != null) {
                    updateUserInfo(wxUser, decryptedUserInfo);
                    System.out.println("使用解密后的信息更新用户");
                } else if (userInfo != null) {
                    updateUserInfo(wxUser, userInfo);
                    System.out.println("使用原始信息更新用户");
                }
            } else {
                // 新用户，创建记录
                wxUser = new WxUser();
                wxUser.setOpenid(openid);
                wxUser.setUnionid(unionid);
                
                // 优先使用解密后的信息，其次使用原始信息
                if (decryptedUserInfo != null) {
                    updateUserInfo(wxUser, decryptedUserInfo);
                    System.out.println("使用解密后的信息创建新用户");
                } else if (userInfo != null) {
                    updateUserInfo(wxUser, userInfo);
                    System.out.println("使用原始信息创建新用户");
                }
            }
            
            // 4. 保存用户信息
            WxUser savedUser = wxUserRepository.save(wxUser);
            System.out.println("用户保存成功，ID: " + savedUser.getId());
            
            return savedUser;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("微信登录失败: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<WxUser> findByOpenid(String openid) {
        return wxUserRepository.findByOpenid(openid);
    }
    
    @Override
    public Optional<WxUser> findById(Long id) {
        return wxUserRepository.findById(id);
    }
    
    @Override
    public WxUser save(WxUser wxUser) {
        return wxUserRepository.save(wxUser);
    }
    
    @Override
    public WxUser updateUser(Long id, WxUser userInfo) {
        Optional<WxUser> existingUser = wxUserRepository.findById(id);
        if (existingUser.isPresent()) {
            WxUser wxUser = existingUser.get();
            updateUserInfo(wxUser, userInfo);
            return wxUserRepository.save(wxUser);
        }
        throw new RuntimeException("用户不存在");
    }
    
    @Override
    public boolean existsByOpenid(String openid) {
        return wxUserRepository.existsByOpenid(openid);
    }
    
    @Override
    public Map<String, String> getSessionInfo(String code) {
        try {
            System.out.println("开始通过code获取session信息，code: " + code);
            
            // 调用微信接口获取session信息
            Map<String, String> wxResponse = getWxSession(code);
            
            if (wxResponse == null || wxResponse.get("openid") == null) {
                throw new RuntimeException("获取微信session信息失败");
            }
            
            String openid = wxResponse.get("openid");
            String sessionKey = wxResponse.get("session_key");
            String unionid = wxResponse.get("unionid");
            
            System.out.println("获取到openid: " + openid);
            System.out.println("获取到session_key: " + (sessionKey != null ? sessionKey.substring(0, 10) + "..." : "null"));
            System.out.println("获取到unionid: " + unionid);
            
            // 返回session信息（包含完整的session_key用于解密）
            Map<String, String> result = new HashMap<>();
            result.put("openid", openid);
            result.put("unionid", unionid != null ? unionid : "");
            result.put("session_key", sessionKey != null ? sessionKey : "");
            
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取session信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 调用微信接口获取session信息
     */
    private Map<String, String> getWxSession(String code) {
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=%s",
                loginUrl, appid, secret, code, grantType);
        
        System.out.println("调用微信接口URL: " + url);
        System.out.println("AppID: " + appid);
        System.out.println("Secret: " + (secret != null ? secret.substring(0, 4) + "****" : "null"));
        
        try {
            // 先获取字符串响应
            String responseString = restTemplate.getForObject(url, String.class);
            System.out.println("微信接口原始返回: " + responseString);
            
            if (responseString == null || responseString.trim().isEmpty()) {
                throw new RuntimeException("微信接口返回空响应");
            }
            
            // 手动解析JSON
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> response = objectMapper.readValue(responseString, Map.class);
            
            System.out.println("微信接口解析后: " + response);
            
            if (response != null && response.get("openid") != null) {
                Map<String, String> result = new HashMap<>();
                result.put("openid", (String) response.get("openid"));
                result.put("unionid", (String) response.get("unionid"));
                result.put("session_key", (String) response.get("session_key"));
                return result;
            } else if (response != null && response.get("errcode") != null) {
                // 微信返回错误
                String errorMsg = "微信接口错误: " + response.get("errcode") + " - " + response.get("errmsg");
                System.out.println(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        } catch (Exception e) {
            System.out.println("调用微信接口异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("调用微信接口失败: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 更新用户信息
     */
    private void updateUserInfo(WxUser target, WxUser source) {
        System.out.println("开始更新用户信息...");
        System.out.println("源用户信息 - 昵称: " + source.getNickname() + ", 头像: " + source.getAvatar() + ", 性别: " + source.getGender());
        
        if (source.getNickname() != null) {
            target.setNickname(source.getNickname());
            System.out.println("更新昵称: " + source.getNickname());
        }
        if (source.getAvatar() != null) {
            target.setAvatar(source.getAvatar());
            System.out.println("更新头像: " + source.getAvatar());
        }
        if (source.getGender() != null) {
            target.setGender(source.getGender());
            System.out.println("更新性别: " + source.getGender());
        }
        if (source.getPhone() != null) {
            target.setPhone(source.getPhone());
            System.out.println("更新手机号: " + source.getPhone());
        }
        if (source.getCountry() != null) {
            target.setCountry(source.getCountry());
            System.out.println("更新国家: " + source.getCountry());
        }
        if (source.getProvince() != null) {
            target.setProvince(source.getProvince());
            System.out.println("更新省份: " + source.getProvince());
        }
        if (source.getCity() != null) {
            target.setCity(source.getCity());
            System.out.println("更新城市: " + source.getCity());
        }
        
        System.out.println("用户信息更新完成");
    }
} 