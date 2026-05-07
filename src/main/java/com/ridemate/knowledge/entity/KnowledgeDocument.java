package com.ridemate.knowledge.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 知识库文档实体类
 */
@Data
public class KnowledgeDocument {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String source;
    private String fileType;
    private Map<String, Object> metadata;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
