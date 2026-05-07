package com.ridemate.knowledge.service.impl;

import com.ridemate.knowledge.model.ParsedDocument;
import com.ridemate.knowledge.service.DocumentParserService;
import com.ridemate.knowledge.service.parser.FileParserFactory;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文档解析服务实现类
 */
@Service
public class DocumentParserServiceImpl implements DocumentParserService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentParserServiceImpl.class);
    private final Tika tika;
    private final FileParserFactory parserFactory;

    @Value("${spring.minio.bucket-name}")
    private String bucketName;

    public DocumentParserServiceImpl(FileParserFactory parserFactory) {
        this.tika = new Tika();
        this.parserFactory = parserFactory;
    }

    @Override
    public ParsedDocument parseDocument(MultipartFile file) throws Exception {
        logger.info("开始解析文档: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
        
        // 检测文件类型
        String mimeType;
        try (InputStream inputStream = file.getInputStream()) {
            mimeType = tika.detect(inputStream);
        }
        logger.info("文件MIME类型: {}", mimeType);
        
        // 获取对应的解析器
        var parser = parserFactory.getParser(mimeType);
        logger.info("使用解析器: {}", parser.getClass().getSimpleName());
        
        // 解析文档
        ParsedDocument parsedDocument = parser.parse(file);
        
        logger.info("文档解析完成: {}, 内容长度: {}, 图片数: {}, 表格数: {}", 
                file.getOriginalFilename(), 
                parsedDocument.getContent().length(), 
                parsedDocument.getImages().size(), 
                parsedDocument.getTables().size());
        
        return parsedDocument;
    }
}
