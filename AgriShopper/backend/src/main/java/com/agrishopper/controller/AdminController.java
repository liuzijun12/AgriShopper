package com.agrishopper.controller;

import com.agrishopper.dto.LoginRequest;
import com.agrishopper.dto.LoginResponse;
import com.agrishopper.model.Admin;
import com.agrishopper.service.AdminService;
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

@Tag(name = "管理员管理", description = "管理员登录和用户管理相关接口")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "管理员登录", description = "管理员用户登录接口")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功"),
        @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = adminService.login(loginRequest);
            return ResponseEntity.ok(createSuccessResponse(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "获取管理员信息", description = "根据令牌获取当前管理员信息")
    @ApiResponse(responseCode = "200", description = "成功获取管理员信息")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            // 从Authorization头中提取token
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                // TODO: 验证token并获取用户信息
                return ResponseEntity.ok(createSuccessResponse("管理员信息"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("无效的认证令牌"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "获取所有管理员", description = "获取系统中的所有管理员列表")
    @ApiResponse(responseCode = "200", description = "成功获取管理员列表")
    @GetMapping("/list")
    public ResponseEntity<?> getAllAdmins() {
        try {
            List<Admin> admins = adminService.getAllAdmins();
            return ResponseEntity.ok(createSuccessResponse(admins));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "创建管理员", description = "创建新的管理员账户")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "管理员创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数有误")
    })
    @PostMapping("/create")
    public ResponseEntity<?> createAdmin(@RequestBody Admin admin) {
        try {
            // 检查手机号是否已存在
            if (adminService.existsByPhone(admin.getPhone())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("手机号已存在"));
            }
            
            Admin savedAdmin = adminService.saveAdmin(admin);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse(savedAdmin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "更新管理员", description = "更新指定管理员的信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "管理员更新成功"),
        @ApiResponse(responseCode = "404", description = "管理员不存在")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdmin(
            @Parameter(description = "管理员ID") @PathVariable Long id,
            @RequestBody Admin admin) {
        try {
            admin.setId(id);
            Admin updatedAdmin = adminService.saveAdmin(admin);
            return ResponseEntity.ok(createSuccessResponse(updatedAdmin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "删除管理员", description = "删除指定的管理员")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "管理员删除成功"),
        @ApiResponse(responseCode = "404", description = "管理员不存在")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(
            @Parameter(description = "管理员ID") @PathVariable Long id) {
        try {
            adminService.deleteAdmin(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
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