package com.ridemate.knowledge.dto;

import lombok.Data;

/**
 * 文档上传请求DTO
 */
@Data
public class UploadDocumentRequest {
    private String title;
    private String category;
    private String source;
}
