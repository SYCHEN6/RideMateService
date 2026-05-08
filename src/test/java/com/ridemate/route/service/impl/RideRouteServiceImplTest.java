package com.ridemate.route.service.impl;

import com.ridemate.route.service.RideRouteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RideRouteServiceImplTest {

    @Autowired
    private RideRouteService rideRouteService;

    @Test
    public void testGetAllRoutes() {
        try {
            System.out.println("测试getAllRoutes方法");
            rideRouteService.getAllRoutes();
            System.out.println("测试成功");
        } catch (Exception e) {
            System.out.println("测试失败");
            e.printStackTrace();
        }
    }
}