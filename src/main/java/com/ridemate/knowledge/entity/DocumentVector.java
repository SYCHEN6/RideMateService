package com.ridemate.knowledge.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档向量实体类
 */
@Data
public class DocumentVector {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 文本片段
     */
    private String content;

    /**
     * 向量
     */
    private float[] vector;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}