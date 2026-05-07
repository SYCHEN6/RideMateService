package com.ridemate.route.service.impl;

import com.ridemate.route.entity.RideRoute;
import com.ridemate.route.dto.RideRouteRequest;
import com.ridemate.route.dto.RideRouteResponse;
import com.ridemate.route.mapper.RideRouteMapper;
import com.ridemate.route.service.RideRouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 骑行路线服务实现类
 */
@Service
public class RideRouteServiceImpl implements RideRouteService {

    private static final Logger logger = LoggerFactory.getLogger(RideRouteServiceImpl.class);

    @Autowired
    private RideRouteMapper rideRouteMapper;

    @Override
    public RideRouteResponse createRoute(RideRouteRequest request, Long creatorId) {
        logger.info("创建路线: {}, 创建者ID: {}", request.getName(), creatorId);
        RideRoute rideRoute = new RideRoute();
        BeanUtils.copyProperties(request, rideRoute);
        rideRoute.setCreatorId(creatorId);
        rideRoute.setCreateTime(LocalDateTime.now());
        rideRoute.setUpdateTime(LocalDateTime.now());

        rideRouteMapper.insert(rideRoute);

        logger.info("路线创建成功: ID={}, 名称={}", rideRoute.getId(), rideRoute.getName());
        return convertToResponse(rideRoute);
    }

    @Override
    public RideRouteResponse getRouteById(Long id) {
        logger.info("根据ID查询路线: ID={}", id);
        RideRoute rideRoute = rideRouteMapper.selectById(id);
        if (rideRoute == null) {
            logger.warn("路线不存在: ID={}", id);
            throw new RuntimeException("路线不存在");
        }

        logger.info("查询路线成功: ID={}, 名称={}", rideRoute.getId(), rideRoute.getName());
        return convertToResponse(rideRoute);
    }

    @Override
    public List<RideRouteResponse> getAllRoutes() {
        logger.info("查询所有路线");
        List<RideRoute> rideRoutes = rideRouteMapper.selectAll();
        logger.info("查询到{}条路线", rideRoutes.size());
        return rideRoutes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RideRouteResponse> getCreatorRoutes(Long creatorId) {
        logger.info("查询创建者的路线: 创建者ID={}", creatorId);
        List<RideRoute> rideRoutes = rideRouteMapper.selectByCreatorId(creatorId);
        logger.info("查询到{}条路线", rideRoutes.size());
        return rideRoutes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RideRouteResponse updateRoute(Long id, RideRouteRequest request) {
        logger.info("更新路线: ID={}, 名称={}", id, request.getName());
        RideRoute rideRoute = rideRouteMapper.selectById(id);
        if (rideRoute == null) {
            logger.warn("路线不存在: ID={}", id);
            throw new RuntimeException("路线不存在");
        }

        BeanUtils.copyProperties(request, rideRoute);
        rideRoute.setUpdateTime(LocalDateTime.now());

        rideRouteMapper.update(rideRoute);

        logger.info("路线更新成功: ID={}", rideRoute.getId());
        return convertToResponse(rideRoute);
    }

    @Override
    public void deleteRoute(Long id) {
        logger.info("删除路线: ID={}", id);
        RideRoute rideRoute = rideRouteMapper.selectById(id);
        if (rideRoute == null) {
            logger.warn("路线不存在: ID={}", id);
            throw new RuntimeException("路线不存在");
        }

        rideRouteMapper.deleteById(id);
        logger.info("路线删除成功: ID={}", id);
    }

    /**
     * 将实体转换为响应DTO
     * @param rideRoute 路线实体
     * @return 路线响应DTO
     */
    private RideRouteResponse convertToResponse(RideRoute rideRoute) {
        RideRouteResponse response = new RideRouteResponse();
        BeanUtils.copyProperties(rideRoute, response);
        return response;
    }
}