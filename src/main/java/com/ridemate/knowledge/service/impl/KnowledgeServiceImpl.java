package com.ridemate.knowledge.service.impl;

import com.ridemate.knowledge.entity.DocumentImage;
import com.ridemate.knowledge.entity.DocumentTable;
import com.ridemate.knowledge.entity.KnowledgeDocument;
import com.ridemate.knowledge.dto.KnowledgeDocumentResponse;
import com.ridemate.knowledge.dto.UploadDocumentRequest;
import com.ridemate.knowledge.mapper.DocumentImageMapper;
import com.ridemate.knowledge.mapper.DocumentTableMapper;
import com.ridemate.knowledge.mapper.KnowledgeDocumentMapper;
import com.ridemate.knowledge.model.ParsedDocument;
import com.ridemate.knowledge.service.KnowledgeService;
import com.ridemate.knowledge.service.DocumentParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识库服务实现类
 */
@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeServiceImpl.class);

    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    private DocumentImageMapper documentImageMapper;

    @Autowired
    private DocumentTableMapper documentTableMapper;

    @Autowired
    private DocumentParserService documentParserService;

    @Value("${spring.minio.bucket-name}")
    private String bucketName;

    @Override
    public KnowledgeDocumentResponse uploadDocument(MultipartFile file, UploadDocumentRequest request) {
        logger.info("上传文档: {}, 分类: {}", file.getOriginalFilename(), request.getCategory());

        try {
            // 解析文档
            ParsedDocument parsedDocument = documentParserService.parseDocument(file);

            // 创建知识库文档
            KnowledgeDocument knowledgeDocument = new KnowledgeDocument();
            knowledgeDocument.setTitle(request.getTitle());
            knowledgeDocument.setContent(parsedDocument.getContent());
            knowledgeDocument.setCategory(request.getCategory());
            knowledgeDocument.setSource(file.getOriginalFilename());
            knowledgeDocument.setFileType(parsedDocument.getFileType().name());
            knowledgeDocument.setCreateTime(LocalDateTime.now());
            knowledgeDocument.setUpdateTime(LocalDateTime.now());

            // 保存文档
            knowledgeDocumentMapper.insert(knowledgeDocument);
            logger.info("文档保存成功: ID={}, 标题={}", knowledgeDocument.getId(), knowledgeDocument.getTitle());

            // 保存图片
            saveImages(parsedDocument, knowledgeDocument.getId());

            // 保存表格
            saveTables(parsedDocument, knowledgeDocument.getId());

            return convertToResponse(knowledgeDocument);
        } catch (Exception e) {
            logger.error("文档上传失败: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("文档上传失败: " + e.getMessage());
        }
    }

    @Override
    public KnowledgeDocumentResponse getDocumentById(Long id) {
        logger.info("根据ID查询文档: ID={}", id);
        KnowledgeDocument document = knowledgeDocumentMapper.selectById(id);
        if (document == null) {
            logger.warn("文档不存在: ID={}", id);
            throw new RuntimeException("文档不存在");
        }

        logger.info("查询文档成功: ID={}, 标题={}", document.getId(), document.getTitle());
        return convertToResponse(document);
    }

    @Override
    public List<KnowledgeDocumentResponse> getAllDocuments() {
        logger.info("查询所有文档");
        List<KnowledgeDocument> documents = knowledgeDocumentMapper.selectAll();
        logger.info("查询到{}篇文档", documents.size());
        return documents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeDocumentResponse> getDocumentsByCategory(String category) {
        logger.info("根据分类查询文档: 分类={}", category);
        List<KnowledgeDocument> documents = knowledgeDocumentMapper.selectByCategory(category);
        logger.info("查询到{}篇文档", documents.size());
        return documents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDocument(Long id) {
        logger.info("删除文档: ID={}", id);
        KnowledgeDocument document = knowledgeDocumentMapper.selectById(id);
        if (document == null) {
            logger.warn("文档不存在: ID={}", id);
            throw new RuntimeException("文档不存在");
        }

        knowledgeDocumentMapper.deleteById(id);
        logger.info("文档删除成功: ID={}", id);
    }

    /**
     * 保存文档中的图片
     * @param parsedDocument 解析后的文档
     * @param documentId 文档ID
     */
    private void saveImages(ParsedDocument parsedDocument, Long documentId) {
        if (parsedDocument.getImages() == null || parsedDocument.getImages().isEmpty()) {
            return;
        }

        logger.info("保存文档图片: 文档ID={}, 图片数量={}", documentId, parsedDocument.getImages().size());
        
        for (ParsedDocument.DocumentImage imageInfo : parsedDocument.getImages()) {
            DocumentImage documentImage = new DocumentImage();
            documentImage.setDocumentId(documentId);
            documentImage.setImagePath(imageInfo.getImagePath());
            documentImage.setDescription(imageInfo.getDescription());
            documentImage.setPageNumber(imageInfo.getPageNumber());
            documentImage.setCreateTime(LocalDateTime.now());

            documentImageMapper.insert(documentImage);
        }
    }

    /**
     * 保存文档中的表格
     * @param parsedDocument 解析后的文档
     * @param documentId 文档ID
     */
    private void saveTables(ParsedDocument parsedDocument, Long documentId) {
        if (parsedDocument.getTables() == null || parsedDocument.getTables().isEmpty()) {
            return;
        }

        logger.info("保存文档表格: 文档ID={}, 表格数量={}", documentId, parsedDocument.getTables().size());
        
        for (ParsedDocument.DocumentTable tableInfo : parsedDocument.getTables()) {
            DocumentTable documentTable = new DocumentTable();
            documentTable.setDocumentId(documentId);
            documentTable.setDescription(tableInfo.getDescription());
            documentTable.setPageNumber(tableInfo.getPageNumber());
            documentTable.setCreateTime(LocalDateTime.now());

            documentTableMapper.insert(documentTable);
        }
    }

    /**
     * 将实体转换为响应DTO
     * @param document 知识库文档实体
     * @return 知识库文档响应DTO
     */
    private KnowledgeDocumentResponse convertToResponse(KnowledgeDocument document) {
        KnowledgeDocumentResponse response = new KnowledgeDocumentResponse();
        BeanUtils.copyProperties(document, response);
        return response;
    }
}