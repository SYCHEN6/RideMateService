package com.ridemate.knowledge.dto;

import lombok.Data;

/**
 * 聊天请求DTO
 */
@Data
public class ChatRequest {
    /**
     * 用户输入的问题
     */
    private String question;

    /**
     * 用户ID（可选）
     */
    private Long userId;

    /**
     * 会话ID（可选，用于多轮对话）
     */
    private String sessionId;

    /**
     * 检索的文档数量限制
     */
    private Integer topK = 5;
}