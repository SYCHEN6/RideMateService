package com.ridemate.route.service;

import com.ridemate.route.dto.RideRouteRequest;
import com.ridemate.route.dto.RideRouteResponse;

import java.util.List;

/**
 * 骑行路线服务接口
 */
public interface RideRouteService {
    /**
     * 创建路线
     * @param request 路线请求DTO
     * @param creatorId 创建者ID
     * @return 路线响应DTO
     */
    RideRouteResponse createRoute(RideRouteRequest request, Long creatorId);

    /**
     * 根据ID获取路线
     * @param id 路线ID
     * @return 路线响应DTO
     */
    RideRouteResponse getRouteById(Long id);

    /**
     * 获取所有路线
     * @return 路线响应DTO列表
     */
    List<RideRouteResponse> getAllRoutes();

    /**
     * 获取创建者的路线
     * @param creatorId 创建者ID
     * @return 路线响应DTO列表
     */
    List<RideRouteResponse> getCreatorRoutes(Long creatorId);

    /**
     * 更新路线
     * @param id 路线ID
     * @param request 路线请求DTO
     * @return 路线响应DTO
     */
    RideRouteResponse updateRoute(Long id, RideRouteRequest request);

    /**
     * 删除路线
     * @param id 路线ID
     */
    void deleteRoute(Long id);
}