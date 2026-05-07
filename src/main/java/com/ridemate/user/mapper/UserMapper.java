package com.ridemate.user.mapper;

import com.ridemate.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    /**
     * 插入用户
     * @param user 用户实体
     * @return 受影响的行数
     */
    int insert(User user);

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户实体
     */
    User selectById(@Param("id") Long id);

    /**
     * 根据用户唯一标识查询用户
     * @param userIdentifier 用户唯一标识
     * @return 用户实体
     */
    User selectByIdentifier(@Param("userIdentifier") String userIdentifier);

    /**
     * 更新用户信息
     * @param user 用户实体
     * @return 受影响的行数
     */
    int update(User user);
}
