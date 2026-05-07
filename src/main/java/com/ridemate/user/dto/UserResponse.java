package com.ridemate.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户响应DTO
 */
@Data
public class UserResponse {
    private Long id;
    private String userIdentifier;
    private String nickname;
    private String avatar;
    private LocalDateTime createTime;
}
