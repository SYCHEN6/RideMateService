package com.ridemate.knowledge.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档图片实体类
 */
@Data
public class DocumentImage {
    private Long id;
    private Long documentId;
    private String imagePath;
    private String description;
    private Integer pageNumber;
    private LocalDateTime createTime;
}
