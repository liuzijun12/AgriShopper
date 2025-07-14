package com.agrishopper.controller;

import com.agrishopper.common.ApiResponse;
import com.agrishopper.model.WxUser;
import com.agrishopper.service.WxUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

/**
 * 微信用户控制器
 */
@RestController
@RequestMapping("/api/wxuser")
@Tag(name = "微信用户", description = "微信用户相关接口")
public class WxUserController {
    
    @Autowired
    private WxUserService wxUserService;
    
    @Value("${wx.appid}")
    private String appid;
    
    @Value("${wx.secret}")
    private String secret;
    
    @Value("${wx.login-url}")
    private String loginUrl;
    
    @Value("${wx.grant-type}")
    private String grantType;
    
    @GetMapping("/test")
    @Operation(summary = "测试接口", description = "测试后端服务是否正常运行")
    public ApiResponse<String> test() {
        System.out.println("测试接口被调用");
        return ApiResponse.success("后端服务正常运行 - " + System.currentTimeMillis());
    }
    
    @GetMapping("/simple-test")
    @Operation(summary = "简单测试接口", description = "不依赖数据库的简单测试")
    public String simpleTest() {
        System.out.println("简单测试接口被调用");
        return "Hello from WxUserController! Time: " + System.currentTimeMillis();
    }
    
    @GetMapping("/ping")
    @Operation(summary = "Ping测试", description = "最简单的ping测试")
    public String ping() {
        return "pong";
    }
    
    @GetMapping("/wx-config")
    @Operation(summary = "微信配置验证", description = "验证微信小程序配置是否正确")
    public ApiResponse<Map<String, String>> checkWxConfig() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("appid", appid != null ? appid.substring(0, Math.min(8, appid.length())) + "****" : "null");
            config.put("secret", secret != null ? secret.substring(0, Math.min(4, secret.length())) + "****" : "null");
            config.put("loginUrl", loginUrl);
            config.put("grantType", grantType);
            config.put("status", "配置正常");
            
            System.out.println("微信配置检查 - AppID: " + (appid != null ? "已配置" : "未配置"));
            System.out.println("微信配置检查 - Secret: " + (secret != null ? "已配置" : "未配置"));
            
            return ApiResponse.success(config);
        } catch (Exception e) {
            return ApiResponse.error(500, "配置检查失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "微信用户登录", description = "通过微信code进行登录或注册")
    public ApiResponse<WxUser> wxLogin(
            @Parameter(description = "微信登录code") @RequestParam String code,
            @Parameter(description = "用户信息（可选）") @RequestBody(required = false) Map<String, Object> requestBody) {
        try {
            System.out.println("=== 开始处理微信登录请求 ===");
            System.out.println("微信登录code: " + code);
            System.out.println("请求体: " + requestBody);

            // 从请求体中提取用户信息
            WxUser userInfo = null;
            if (requestBody != null && requestBody.containsKey("userInfo")) {
                Map<String, Object> userInfoMap = (Map<String, Object>) requestBody.get("userInfo");
                System.out.println("接收到用户信息Map: " + userInfoMap);

                userInfo = new WxUser();

                // 兼容微信字段名（小写）
                if (userInfoMap.containsKey("nickName")) {
                    String nickname = (String) userInfoMap.get("nickName");
                    userInfo.setNickname(nickname);
                    System.out.println("设置昵称: " + nickname);
                }
                if (userInfoMap.containsKey("avatarUrl")) {
                    String avatar = (String) userInfoMap.get("avatarUrl");
                    userInfo.setAvatar(avatar);
                    System.out.println("设置头像: " + avatar);
                }
                if (userInfoMap.containsKey("gender")) {
                    Integer gender = 0;
                    Object genderObj = userInfoMap.get("gender");
                    if (genderObj instanceof Integer) {
                        gender = (Integer) genderObj;
                    } else if (genderObj instanceof Number) {
                        gender = ((Number) genderObj).intValue();
                    }
                    userInfo.setGender(gender);
                    System.out.println("设置性别: " + gender);
                }
                if (userInfoMap.containsKey("country")) {
                    String country = (String) userInfoMap.get("country");
                    userInfo.setCountry(country);
                    System.out.println("设置国家: " + country);
                }
                if (userInfoMap.containsKey("province")) {
                    String province = (String) userInfoMap.get("province");
                    userInfo.setProvince(province);
                    System.out.println("设置省份: " + province);
                }
                if (userInfoMap.containsKey("city")) {
                    String city = (String) userInfoMap.get("city");
                    userInfo.setCity(city);
                    System.out.println("设置城市: " + city);
                }
                // 你可以根据WxUser模型继续补充其他字段
                System.out.println("解析后的用户信息: " + userInfo.getNickname());
            } else {
                System.out.println("请求体中没有userInfo字段或请求体为空");
            }

            // 调用服务层处理登录
            WxUser wxUser = wxUserService.wxLogin(code, userInfo);
            System.out.println("登录成功，返回用户信息: " + wxUser.getNickname());
            System.out.println("=== 微信登录请求处理完成 ===");

            return ApiResponse.success(wxUser);
        } catch (Exception e) {
            System.out.println("=== 微信登录请求处理失败 ===");
            e.printStackTrace();
            return ApiResponse.error(500, "登录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户信息", description = "通过用户ID获取微信用户信息")
    public ApiResponse<WxUser> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        try {
            Optional<WxUser> wxUser = wxUserService.findById(id);
            if (wxUser.isPresent()) {
                return ApiResponse.success(wxUser.get());
            } else {
                return ApiResponse.error(404, "用户不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "获取用户信息失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/openid/{openid}")
    @Operation(summary = "根据openid获取用户信息", description = "通过微信openid获取用户信息")
    public ApiResponse<WxUser> getUserByOpenid(
            @Parameter(description = "微信openid") @PathVariable String openid) {
        try {
            Optional<WxUser> wxUser = wxUserService.findByOpenid(openid);
            if (wxUser.isPresent()) {
                return ApiResponse.success(wxUser.get());
            } else {
                return ApiResponse.error(404, "用户不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "获取用户信息失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "更新微信用户信息")
    public ApiResponse<WxUser> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "更新的用户信息") @RequestBody WxUser userInfo) {
        try {
            WxUser wxUser = wxUserService.updateUser(id, userInfo);
            return ApiResponse.success(wxUser);
        } catch (Exception e) {
            return ApiResponse.error(500, "更新失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/check/{openid}")
    @Operation(summary = "检查用户是否存在", description = "检查指定openid的用户是否存在")
    public ApiResponse<Boolean> checkUserExists(
            @Parameter(description = "微信openid") @PathVariable String openid) {
        try {
            boolean exists = wxUserService.existsByOpenid(openid);
            return ApiResponse.success(exists);
        } catch (Exception e) {
            return ApiResponse.error(500, "检查失败: " + e.getMessage());
        }
    }
} 