package com.ridemate.knowledge.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 文档表格实体类
 */
@Data
public class DocumentTable {
    private Long id;
    private Long documentId;
    private Map<String, Object> tableData;
    private String description;
    private Integer pageNumber;
    private LocalDateTime createTime;
}
