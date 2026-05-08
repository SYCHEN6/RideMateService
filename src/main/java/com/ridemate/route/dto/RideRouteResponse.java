package com.ridemate.route.dto;

import lombok.Data;

import java.util.Date;

/**
 * 骑行路线响应DTO
 */
@Data
public class RideRouteResponse {
    private Long id;
    private String name;
    private String description;
    private String startPoint;
    private String endPoint;
    private Double distance;
    private Integer duration;
    private String difficulty;
    private Integer elevationGain;
    private Long creatorId;
    private Date createTime;
    private Date updateTime;
}