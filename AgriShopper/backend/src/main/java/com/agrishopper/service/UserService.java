package com.agrishopper.service;

import com.agrishopper.model.User;
import com.agrishopper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务层
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 创建新用户
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 更新用户信息
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * 根据ID查询用户
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据OpenID查询用户
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByOpenId(String openId) {
        return userRepository.findByOpenId(openId);
    }

    /**
     * 根据用户名查询用户
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 查询所有用户
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 检查OpenID是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByOpenId(String openId) {
        return userRepository.existsByOpenId(openId);
    }

    /**
     * 检查用户名是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 检查手机号是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
} 