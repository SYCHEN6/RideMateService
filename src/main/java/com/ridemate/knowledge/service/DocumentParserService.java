package com.ridemate.knowledge.service;

import com.ridemate.knowledge.model.ParsedDocument;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档解析服务接口
 */
public interface DocumentParserService {
    /**
     * 解析文档
     * @param file 上传的文件
     * @return 解析后的文档对象
     * @throws Exception 解析异常
     */
    ParsedDocument parseDocument(MultipartFile file) throws Exception;
}
