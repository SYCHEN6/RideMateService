package com.ridemate.route.entity;

import lombok.Data;

import java.util.Date;

/**
 * 骑行路线实体类
 */
@Data
public class RideRoute {
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