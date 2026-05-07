package com.ridemate.knowledge.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 知识库文档响应DTO
 */
@Data
public class KnowledgeDocumentResponse {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String source;
    private String fileType;
    private List<DocumentImageResponse> images;
    private List<DocumentTableResponse> tables;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 文档图片响应DTO
     */
    @Data
    public static class DocumentImageResponse {
        private Long id;
        private String imagePath;
        private String description;
        private Integer pageNumber;
    }

    /**
     * 文档表格响应DTO
     */
    @Data
    public static class DocumentTableResponse {
        private Long id;
        private Map<String, Object> tableData;
        private String description;
        private Integer pageNumber;
    }
}
