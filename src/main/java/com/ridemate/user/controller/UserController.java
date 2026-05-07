package com.ridemate.user.controller;

import com.ridemate.user.dto.UserResponse;
import com.ridemate.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 创建匿名用户
     * @return 用户响应
     */
    @PostMapping("/anonymous")
    public ResponseEntity<UserResponse> createAnonymousUser() {
        UserResponse user = userService.createAnonymousUser();
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户响应
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        try {
            UserResponse user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param nickname 用户昵称
     * @param avatar 用户头像
     * @return 更新后的用户响应
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String avatar) {
        try {
            UserResponse user = userService.updateUser(id, nickname, avatar);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
