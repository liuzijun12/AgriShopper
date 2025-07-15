package com.agrishopper.service.impl;

import com.agrishopper.dto.LoginRequest;
import com.agrishopper.dto.LoginResponse;
import com.agrishopper.model.Admin;
import com.agrishopper.repository.AdminRepository;
import com.agrishopper.service.AdminService;
import com.agrishopper.utils.JwtUtil;
import com.agrishopper.utils.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        System.out.println("=== 开始登录流程 ===");
        System.out.println("登录请求账号: " + loginRequest.getAccount());
        System.out.println("登录请求密码: " + loginRequest.getPassword());
        
        // 先尝试通过手机号查找管理员
        Optional<Admin> adminOpt = adminRepository.findByPhone(loginRequest.getAccount());
        System.out.println("通过手机号查找结果: " + (adminOpt.isPresent() ? "找到" : "未找到"));
        
        // 如果手机号没找到，尝试通过昵称查找
        if (adminOpt.isEmpty()) {
            System.out.println("尝试通过昵称查找...");
            adminOpt = adminRepository.findByNickname(loginRequest.getAccount());
            System.out.println("通过昵称查找结果: " + (adminOpt.isPresent() ? "找到" : "未找到"));
        }
        
        if (adminOpt.isEmpty()) {
            System.out.println("❌ 账号不存在");
            throw new RuntimeException("账号或密码错误");
        }
        
        Admin admin = adminOpt.get();
        System.out.println("✅ 找到管理员: " + admin.getNickname());
        System.out.println("数据库中的密码: " + admin.getPassword());
        System.out.println("输入的密码: " + loginRequest.getPassword());
        System.out.println("密码是否匹配: " + loginRequest.getPassword().equals(admin.getPassword()));
        
        // 验证密码（暂时使用明文比较，后续可以改为加密）
        if (!loginRequest.getPassword().equals(admin.getPassword())) {
            System.out.println("❌ 密码验证失败");
            throw new RuntimeException("账号或密码错误");
        }
        
        System.out.println("✅ 密码验证成功");
        
        try {
            // 生成JWT令牌
            System.out.println("开始生成JWT令牌...");
            String accessToken = jwtUtil.generateToken(admin.getPhone(), admin.getId());
            System.out.println("✅ JWT令牌生成成功: " + accessToken.substring(0, 20) + "...");
            
            // 构建登录响应
            System.out.println("开始构建登录响应...");
            LoginResponse.AdminInfo adminInfo = LoginResponse.AdminInfo.builder()
                    .id(admin.getId())
                    .nickname(admin.getNickname())
                    .phone(admin.getPhone())
                    .gender(admin.getGender())
                    .build();
            System.out.println("✅ AdminInfo构建成功");
            
            LoginResponse response = LoginResponse.builder()
                    .accessToken(accessToken)
                    .tokenType("Bearer")
                    .expiresIn(3600L)
                    .adminInfo(adminInfo)
                    .build();
            System.out.println("✅ LoginResponse构建成功");
            
            System.out.println("✅ 登录成功，返回响应");
            System.out.println("=== 登录流程结束 ===");
            
            return response;
        } catch (Exception e) {
            System.out.println("❌ 构建响应时发生异常: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    @Override
    public Optional<Admin> getAdminByPhone(String phone) {
        return adminRepository.findByPhone(phone);
    }

    @Override
    public Optional<Admin> getAdminByNickname(String nickname) {
        return adminRepository.findByNickname(nickname);
    }

    @Override
    public Admin saveAdmin(Admin admin) {
        // 如果是新管理员，加密密码
        if (admin.getId() == null && admin.getPassword() != null) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        return adminRepository.save(admin);
    }

    @Override
    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return adminRepository.existsByPhone(phone);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return adminRepository.existsByNickname(nickname);
    }
} 