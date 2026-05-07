package com.ridemate.knowledge.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档表格实体类
 */
@Data
public class DocumentTable {
    private Long id;
    private Long documentId;
    private String tableData;
    private String description;
    private Integer pageNumber;
    private LocalDateTime createTime;
}
