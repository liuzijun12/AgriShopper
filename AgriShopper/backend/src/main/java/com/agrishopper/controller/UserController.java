package com.agrishopper.controller;

import com.agrishopper.model.User;
import com.agrishopper.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "用户管理相关接口，包括用户的增删改查等操作")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "创建新用户", description = "创建一个新的用户账号")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户创建成功"),
        @ApiResponse(responseCode = "400", description = "用户创建失败，请求参数有误")
    })
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createSuccessResponse(createdUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "更新用户信息", description = "根据用户ID更新用户信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户信息更新成功"),
        @ApiResponse(responseCode = "404", description = "未找到指定用户")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody User user) {
        return userService.getUserById(id)
                .map(existingUser -> {
                    user.setId(id);
                    User updatedUser = userService.updateUser(user);
                    return ResponseEntity.ok(createSuccessResponse(updatedUser));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("未找到指定用户")));
    }

    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户删除成功"),
        @ApiResponse(responseCode = "404", description = "未找到指定用户")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.ok(createSuccessResponse(null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("未找到指定用户"));
    }

    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息"),
        @ApiResponse(responseCode = "404", description = "未找到指定用户")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(createSuccessResponse(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("未找到指定用户")));
    }

    @Operation(summary = "根据OpenID获取用户", description = "根据微信OpenID获取用户信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息"),
        @ApiResponse(responseCode = "404", description = "未找到指定用户")
    })
    @GetMapping("/openid/{openId}")
    public ResponseEntity<?> getUserByOpenId(
            @Parameter(description = "微信OpenID") @PathVariable String openId) {
        return userService.getUserByOpenId(openId)
                .map(user -> ResponseEntity.ok(createSuccessResponse(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("未找到指定用户")));
    }

    @Operation(summary = "获取所有用户", description = "获取系统中所有用户的列表")
    @ApiResponse(responseCode = "200", description = "成功获取用户列表")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(createSuccessResponse(users));
    }

    @Operation(summary = "检查OpenID是否存在", description = "检查系统中是否已存在该OpenID")
    @ApiResponse(responseCode = "200", description = "检查完成")
    @GetMapping("/check/openid/{openId}")
    public ResponseEntity<?> checkOpenIdExists(
            @Parameter(description = "微信OpenID") @PathVariable String openId) {
        boolean exists = userService.existsByOpenId(openId);
        return ResponseEntity.ok(createSuccessResponse(exists));
    }

    @Operation(summary = "检查用户名是否存在", description = "检查系统中是否已存在该用户名")
    @ApiResponse(responseCode = "200", description = "检查完成")
    @GetMapping("/check/username/{username}")
    public ResponseEntity<?> checkUsernameExists(
            @Parameter(description = "用户名") @PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(createSuccessResponse(exists));
    }

    @Operation(summary = "检查邮箱是否存在", description = "检查系统中是否已存在该邮箱")
    @ApiResponse(responseCode = "200", description = "检查完成")
    @GetMapping("/check/email/{email}")
    public ResponseEntity<?> checkEmailExists(
            @Parameter(description = "邮箱地址") @PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(createSuccessResponse(exists));
    }

    @Operation(summary = "检查手机号是否存在", description = "检查系统中是否已存在该手机号")
    @ApiResponse(responseCode = "200", description = "检查完成")
    @GetMapping("/check/phone/{phone}")
    public ResponseEntity<?> checkPhoneExists(
            @Parameter(description = "手机号") @PathVariable String phone) {
        boolean exists = userService.existsByPhone(phone);
        return ResponseEntity.ok(createSuccessResponse(exists));
    }

    private Map<String, Object> createSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        response.put("data", null);
        return response;
    }
} 