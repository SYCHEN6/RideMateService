package com.ridemate.knowledge.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析后的文档模型
 */
@Data
@NoArgsConstructor
public class ParsedDocument {
    private FileType fileType;
    private String content;
    private List<DocumentImage> images = new ArrayList<>();
    private List<DocumentTable> tables = new ArrayList<>();

    public void addImage(DocumentImage image) {
        this.images.add(image);
    }

    public void addTable(DocumentTable table) {
        this.tables.add(table);
    }

    /**
     * 文件类型枚举
     */
    public enum FileType {
        PDF, WORD, EXCEL, PPT, IMAGE, TEXT, UNKNOWN
    }

    /**
     * 文档图片模型
     */
    @Data
    public static class DocumentImage {
        private String imagePath;
        private String description;
        private Integer pageNumber;
    }

    /**
     * 文档表格模型
     */
    @Data
    public static class DocumentTable {
        private String description;
        private Integer pageNumber;
    }
}
