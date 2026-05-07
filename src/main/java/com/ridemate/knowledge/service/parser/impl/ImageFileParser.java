package com.ridemate.knowledge.service.parser.impl;

import com.ridemate.knowledge.model.ParsedDocument;
import com.ridemate.knowledge.service.parser.FileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片文件解析器实现类
 */
@Component
public class ImageFileParser implements FileParser {

    private static final Logger logger = LoggerFactory.getLogger(ImageFileParser.class);

    @Override
    public ParsedDocument parse(MultipartFile file) throws Exception {
        logger.info("开始解析图片文件: {}", file.getOriginalFilename());
        ParsedDocument parsedDocument = new ParsedDocument();
        parsedDocument.setFileType(ParsedDocument.FileType.IMAGE);

        try {
            // 图片OCR识别（后续实现）
            parsedDocument.setContent("[图片内容，待OCR识别]");
            logger.info("图片文件解析成功: {}", file.getOriginalFilename());
        } catch (Exception e) {
            logger.error("解析图片文件失败: {}", file.getOriginalFilename(), e);
            throw e;
        }

        return parsedDocument;
    }

    @Override
    public ParsedDocument.FileType getSupportedFileType() {
        return ParsedDocument.FileType.IMAGE;
    }
}
