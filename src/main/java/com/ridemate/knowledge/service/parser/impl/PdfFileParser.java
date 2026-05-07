package com.ridemate.knowledge.service.parser.impl;

import com.ridemate.knowledge.model.ParsedDocument;
import com.ridemate.knowledge.service.parser.FileParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * PDF文件解析器实现类
 */
@Component
public class PdfFileParser implements FileParser {

    private static final Logger logger = LoggerFactory.getLogger(PdfFileParser.class);

    @Override
    public ParsedDocument parse(MultipartFile file) throws Exception {
        logger.info("开始解析PDF文件: {}", file.getOriginalFilename());
        ParsedDocument parsedDocument = new ParsedDocument();
        parsedDocument.setFileType(ParsedDocument.FileType.PDF);

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            parsedDocument.setContent(text);
            logger.info("PDF文件解析成功: {}, 内容长度: {}", file.getOriginalFilename(), text.length());

            // 提取图片（后续实现）
            // extractImagesFromPdf(document, parsedDocument);

            // 提取表格（后续实现）
            // extractTablesFromPdf(document, parsedDocument);
        } catch (Exception e) {
            logger.error("解析PDF文件失败: {}", file.getOriginalFilename(), e);
            throw e;
        }

        return parsedDocument;
    }

    @Override
    public ParsedDocument.FileType getSupportedFileType() {
        return ParsedDocument.FileType.PDF;
    }
}
