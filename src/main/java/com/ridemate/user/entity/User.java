package com.ridemate.user.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    private Long id;
    private String userIdentifier;
    private String nickname;
    private String avatar;
    private LocalDateTime createTime;
}
