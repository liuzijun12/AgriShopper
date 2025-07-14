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

/**
 * 微信用户服务实现类
 */
@Service
public class WxUserServiceImpl implements WxUserService {
    
    @Autowired
    private WxUserRepository wxUserRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
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
        try {
            System.out.println("开始处理微信登录，code: " + code);
            
            // 1. 通过code获取openid和session_key
            Map<String, String> wxResponse = getWxSession(code);
            
            if (wxResponse == null || wxResponse.get("openid") == null) {
                throw new RuntimeException("获取微信用户信息失败");
            }
            
            String openid = wxResponse.get("openid");
            String unionid = wxResponse.get("unionid");
            
            System.out.println("获取到openid: " + openid);
            System.out.println("获取到unionid: " + unionid);
            
            // 2. 查找或创建用户
            Optional<WxUser> existingUser = wxUserRepository.findByOpenid(openid);
            WxUser wxUser;
            
            if (existingUser.isPresent()) {
                // 用户已存在，更新信息
                wxUser = existingUser.get();
                System.out.println("用户已存在，ID: " + wxUser.getId());
                if (userInfo != null) {
                    updateUserInfo(wxUser, userInfo);
                }
            } else {
                // 新用户，创建记录
                wxUser = new WxUser();
                wxUser.setOpenid(openid);
                wxUser.setUnionid(unionid);
                if (userInfo != null) {
                    updateUserInfo(wxUser, userInfo);
                }
                System.out.println("创建新用户");
            }
            
            // 3. 保存用户信息
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