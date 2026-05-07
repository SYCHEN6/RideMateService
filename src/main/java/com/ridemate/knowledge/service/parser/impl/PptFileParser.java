package com.ridemate.knowledge.service.parser.impl;

import com.ridemate.knowledge.model.ParsedDocument;
import com.ridemate.knowledge.service.parser.FileParser;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * PPT文件解析器实现类
 */
@Component
public class PptFileParser implements FileParser {

    private static final Logger logger = LoggerFactory.getLogger(PptFileParser.class);
    private final Tika tika = new Tika();
    private final AutoDetectParser parser = new AutoDetectParser();

    @Override
    public ParsedDocument parse(MultipartFile file) throws Exception {
        logger.info("开始解析PPT文件: {}", file.getOriginalFilename());
        ParsedDocument parsedDocument = new ParsedDocument();
        parsedDocument.setFileType(ParsedDocument.FileType.PPT);

        try (InputStream stream = file.getInputStream()) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            parser.parse(stream, handler, metadata, context);
            parsedDocument.setContent(handler.toString());
            logger.info("PPT文件解析成功: {}, 内容长度: {}", file.getOriginalFilename(), handler.toString().length());

            // 提取图片（后续实现）
        } catch (Exception e) {
            logger.error("解析PPT文件失败: {}", file.getOriginalFilename(), e);
            throw e;
        }

        return parsedDocument;
    }

    @Override
    public ParsedDocument.FileType getSupportedFileType() {
        return ParsedDocument.FileType.PPT;
    }
}
