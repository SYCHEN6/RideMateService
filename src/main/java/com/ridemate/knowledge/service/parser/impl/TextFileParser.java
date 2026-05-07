package com.ridemate.knowledge.service.parser.impl;

import com.ridemate.knowledge.model.ParsedDocument;
import com.ridemate.knowledge.service.parser.FileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文本文件解析器实现类
 */
@Component
public class TextFileParser implements FileParser {

    private static final Logger logger = LoggerFactory.getLogger(TextFileParser.class);

    @Override
    public ParsedDocument parse(MultipartFile file) throws Exception {
        logger.info("开始解析文本文件: {}", file.getOriginalFilename());
        ParsedDocument parsedDocument = new ParsedDocument();
        parsedDocument.setFileType(ParsedDocument.FileType.TEXT);

        try {
            String content = new String(file.getBytes());
            parsedDocument.setContent(content);
            logger.info("文本文件解析成功: {}, 内容长度: {}", file.getOriginalFilename(), content.length());
        } catch (Exception e) {
            logger.error("解析文本文件失败: {}", file.getOriginalFilename(), e);
            throw e;
        }

        return parsedDocument;
    }

    @Override
    public ParsedDocument.FileType getSupportedFileType() {
        return ParsedDocument.FileType.TEXT;
    }
}
