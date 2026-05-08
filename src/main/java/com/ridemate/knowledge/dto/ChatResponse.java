package com.ridemate.knowledge.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天响应DTO
 */
@Data
public class ChatResponse {
    /**
     * 问题
     */
    private String question;

    /**
     * 回答
     */
    private String answer;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 回答时间
     */
    private LocalDateTime timestamp;

    /**
     * 引用的文档片段
     */
    private List<ReferenceDocument> references;

    /**
     * 引用文档信息
     */
    @Data
    public static class ReferenceDocument {
        /**
         * 文档ID
         */
        private Long documentId;

        /**
         * 文档标题
         */
        private String documentTitle;

        /**
         * 引用的文本内容
         */
        private String content;

        /**
         * 相似度分数
         */
        private Double similarityScore;
    }
}