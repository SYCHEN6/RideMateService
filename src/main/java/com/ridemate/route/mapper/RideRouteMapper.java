package com.ridemate.route.mapper;

import com.ridemate.route.entity.RideRoute;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 骑行路线Mapper接口
 */
@Mapper
public interface RideRouteMapper {
    /**
     * 插入路线
     * @param rideRoute 路线实体
     * @return 影响的行数
     */
    int insert(RideRoute rideRoute);

    /**
     * 根据ID查询路线
     * @param id 路线ID
     * @return 路线实体
     */
    RideRoute selectById(Long id);

    /**
     * 查询所有路线
     * @return 路线列表
     */
    List<RideRoute> selectAll();

    /**
     * 根据创建者ID查询路线
     * @param creatorId 创建者ID
     * @return 路线列表
     */
    List<RideRoute> selectByCreatorId(Long creatorId);

    /**
     * 更新路线
     * @param rideRoute 路线实体
     * @return 影响的行数
     */
    int update(RideRoute rideRoute);

    /**
     * 删除路线
     * @param id 路线ID
     * @return 影响的行数
     */
    int deleteById(Long id);
}