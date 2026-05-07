package com.ridemate.knowledge.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档上传请求DTO
 */
@Data
public class UploadDocumentRequest {
    private MultipartFile file;
    private String category;
    private String source;
}
