package com.ridemate.route.dto;

import lombok.Data;

/**
 * 骑行路线请求DTO
 */
@Data
public class RideRouteRequest {
    private String name;
    private String description;
    private String startPoint;
    private String endPoint;
    private Double distance;
    private Integer duration;
    private String difficulty;
    private Integer elevationGain;
}