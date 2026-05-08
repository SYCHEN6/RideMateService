package com.ridemate.route.controller;

import com.ridemate.route.dto.RideRouteRequest;
import com.ridemate.route.dto.RideRouteResponse;
import com.ridemate.route.service.RideRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 骑行路线控制器
 */
@RestController
@RequestMapping("/routes")
public class RideRouteController {

    @Autowired
    private RideRouteService rideRouteService;

    /**
     * 创建路线
     * @param request 路线请求DTO
     * @return 路线响应
     */
    @PostMapping
    public ResponseEntity<RideRouteResponse> createRoute(@RequestBody(required = false) RideRouteRequest request, @RequestHeader(value = "X-User-Id", defaultValue = "1") Long creatorId) {
        try {
            if (request == null) {
                System.out.println("请求体为null");
                return ResponseEntity.badRequest().build();
            }
            System.out.println("收到创建路线请求: " + request);
            System.out.println("创建者ID: " + creatorId);
            RideRouteResponse route = rideRouteService.createRoute(request, creatorId);
            System.out.println("路线创建成功: " + route);
            return new ResponseEntity<>(route, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("创建路线失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据ID获取路线
     * @param id 路线ID
     * @return 路线响应
     */
    @GetMapping("/{id}")
    public ResponseEntity<RideRouteResponse> getRouteById(@PathVariable Long id) {
        try {
            RideRouteResponse route = rideRouteService.getRouteById(id);
            return ResponseEntity.ok(route);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取所有路线
     * @return 路线响应列表
     */
    @GetMapping
    public ResponseEntity<List<RideRouteResponse>> getAllRoutes() {
        try {
            List<RideRouteResponse> routes = rideRouteService.getAllRoutes();
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取创建者的路线
     * @param creatorId 创建者ID
     * @return 路线响应列表
     */
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<RideRouteResponse>> getCreatorRoutes(@PathVariable Long creatorId) {
        List<RideRouteResponse> routes = rideRouteService.getCreatorRoutes(creatorId);
        return ResponseEntity.ok(routes);
    }

    /**
     * 更新路线
     * @param id 路线ID
     * @param request 路线请求DTO
     * @return 路线响应
     */
    @PutMapping("/{id}")
    public ResponseEntity<RideRouteResponse> updateRoute(@PathVariable Long id, @RequestBody RideRouteRequest request) {
        try {
            RideRouteResponse route = rideRouteService.updateRoute(id, request);
            return ResponseEntity.ok(route);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除路线
     * @param id 路线ID
     * @return 响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        try {
            rideRouteService.deleteRoute(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}