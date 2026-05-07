package com.ridemate.user.service.impl;

import com.ridemate.user.entity.User;
import com.ridemate.user.dto.UserResponse;
import com.ridemate.user.mapper.UserMapper;
import com.ridemate.user.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserResponse createAnonymousUser() {
        logger.info("创建匿名用户");
        User user = new User();
        user.setUserIdentifier(UUID.randomUUID().toString());
        user.setNickname("匿名用户" + RandomStringUtils.randomNumeric(6));
        
        userMapper.insert(user);
        
        logger.info("匿名用户创建成功: ID={}, 昵称={}", user.getId(), user.getNickname());
        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        logger.info("根据ID查询用户: ID={}", id);
        User user = userMapper.selectById(id);
        if (user == null) {
            logger.warn("用户不存在: ID={}", id);
            throw new RuntimeException("用户不存在");
        }
        
        logger.info("查询用户成功: ID={}, 昵称={}", user.getId(), user.getNickname());
        return convertToResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, String nickname, String avatar) {
        logger.info("更新用户信息: ID={}, 昵称={}", id, nickname);
        User user = userMapper.selectById(id);
        if (user == null) {
            logger.warn("用户不存在: ID={}", id);
            throw new RuntimeException("用户不存在");
        }
        
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        
        userMapper.update(user);
        
        logger.info("用户信息更新成功: ID={}", id);
        return convertToResponse(user);
    }

    /**
     * 将User实体转换为UserResponse DTO
     * @param user 用户实体
     * @return 用户响应DTO
     */
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}
