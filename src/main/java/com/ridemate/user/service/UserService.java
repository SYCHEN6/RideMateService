package com.ridemate.user.service;

import com.ridemate.user.dto.UserResponse;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 创建匿名用户
     * @return 用户响应DTO
     */
    UserResponse createAnonymousUser();

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户响应DTO
     */
    UserResponse getUserById(Long id);

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param nickname 用户昵称
     * @param avatar 用户头像
     * @return 更新后的用户响应DTO
     */
    UserResponse updateUser(Long id, String nickname, String avatar);
}
