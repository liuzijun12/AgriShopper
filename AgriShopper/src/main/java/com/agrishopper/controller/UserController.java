package com.agrishopper.controller;

import com.agrishopper.common.ApiResponse;
import com.agrishopper.model.User;
import com.agrishopper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(ApiResponse.success(createdUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.getUserById(id)
                .map(existingUser -> {
                    user.setId(id);
                    User updatedUser = userService.updateUser(user);
                    return ResponseEntity.ok(ApiResponse.success(updatedUser));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "User not found")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "User not found"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(ApiResponse.success(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "User not found")));
    }

    @GetMapping("/openid/{openId}")
    public ResponseEntity<ApiResponse<User>> getUserByOpenId(@PathVariable String openId) {
        return userService.getUserByOpenId(openId)
                .map(user -> ResponseEntity.ok(ApiResponse.success(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "User not found")));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<User>> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(user -> ResponseEntity.ok(ApiResponse.success(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "User not found")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/check/openid/{openId}")
    public ResponseEntity<ApiResponse<Boolean>> checkOpenIdExists(@PathVariable String openId) {
        boolean exists = userService.existsByOpenId(openId);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/check/username/{username}")
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameExists(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/check/email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/check/phone/{phone}")
    public ResponseEntity<ApiResponse<Boolean>> checkPhoneExists(@PathVariable String phone) {
        boolean exists = userService.existsByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
} 