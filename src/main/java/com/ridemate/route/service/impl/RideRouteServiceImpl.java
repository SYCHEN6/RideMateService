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

import java.util.Collections;
import java.util.Date;
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
        logger.info("开始创建路线");
        logger.info("完整请求参数: {}", request);
        logger.info("创建者ID: {}", creatorId);
        try {
            RideRoute rideRoute = new RideRoute();
            logger.info("创建RideRoute实体");
            BeanUtils.copyProperties(request, rideRoute);
            logger.info("复制属性后: {}", rideRoute);
            rideRoute.setCreatorId(creatorId);
            rideRoute.setCreateTime(new Date());
            rideRoute.setUpdateTime(new Date());
            logger.info("设置创建者和时间后: {}", rideRoute);

            rideRouteMapper.insert(rideRoute);
            logger.info("路线插入数据库成功，ID: {}", rideRoute.getId());

            RideRouteResponse response = convertToResponse(rideRoute);
            logger.info("转换为响应对象: {}", response);
            return response;
        } catch (Exception e) {
            logger.error("创建路线失败: {}", e.getMessage(), e);
            throw e;
        }
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
        try {
            // 添加更详细的日志记录，找出问题所在
            logger.info("调用rideRouteMapper.selectAll()方法");
            List<RideRoute> rideRoutes = rideRouteMapper.selectAll();
            logger.info("rideRouteMapper.selectAll()方法返回结果: {}", rideRoutes);
            if (rideRoutes == null) {
                logger.info("rideRoutes为null，返回空列表");
                return Collections.emptyList();
            }
            logger.info("rideRoutes不为null，转换为响应DTO列表");
            return rideRoutes.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("查询所有路线失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
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
        rideRoute.setUpdateTime(new Date());

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