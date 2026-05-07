package com.ridemate.knowledge.service.parser;

import com.ridemate.knowledge.model.ParsedDocument;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件解析器工厂类
 */
@Component
public class FileParserFactory {

    private final Tika tika = new Tika();
    private final Map<ParsedDocument.FileType, FileParser> parserMap = new ConcurrentHashMap<>();

    /**
     * 构造函数，自动注入所有FileParser实现类
     * @param parsers 所有FileParser实现类的列表
     */
    public FileParserFactory(List<FileParser> parsers) {
        for (FileParser parser : parsers) {
            parserMap.put(parser.getSupportedFileType(), parser);
        }
    }

    /**
     * 根据文件内容获取对应的解析器
     * @param mimeType 文件的MIME类型
     * @return 对应的文件解析器
     */
    public FileParser getParser(String mimeType) {
        ParsedDocument.FileType fileType = getFileType(mimeType);
        FileParser parser = parserMap.get(fileType);
        
        // 如果没有找到特定类型的解析器，使用默认解析器
        if (parser == null) {
            parser = parserMap.get(ParsedDocument.FileType.UNKNOWN);
        }
        
        return parser;
    }

    /**
     * 根据MIME类型获取文件类型
     */
    private ParsedDocument.FileType getFileType(String mimeType) {
        return switch (mimeType) {
            case "application/pdf" -> ParsedDocument.FileType.PDF;
            case "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> 
                ParsedDocument.FileType.WORD;
            case "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> 
                ParsedDocument.FileType.EXCEL;
            case "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> 
                ParsedDocument.FileType.PPT;
            case "image/jpeg", "image/png", "image/gif" -> ParsedDocument.FileType.IMAGE;
            case "text/plain", "text/html", "text/markdown" -> ParsedDocument.FileType.TEXT;
            default -> ParsedDocument.FileType.UNKNOWN;
        };
    }
}
