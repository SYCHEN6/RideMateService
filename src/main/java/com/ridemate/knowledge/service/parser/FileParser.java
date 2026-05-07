package com.ridemate.knowledge.service.parser;

import com.ridemate.knowledge.model.ParsedDocument;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件解析器接口
 */
public interface FileParser {
    /**
     * 解析文件
     * @param file 上传的文件
     * @return 解析后的文档对象
     * @throws Exception 解析异常
     */
    ParsedDocument parse(MultipartFile file) throws Exception;

    /**
     * 获取支持的文件类型
     * @return 文件类型
     */
    ParsedDocument.FileType getSupportedFileType();
}
