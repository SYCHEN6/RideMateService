package com.ridemate.knowledge.service.impl;

import com.ridemate.knowledge.dto.ChatRequest;
import com.ridemate.knowledge.dto.ChatResponse;
import com.ridemate.knowledge.dto.KnowledgeDocumentResponse;
import com.ridemate.knowledge.dto.UploadDocumentRequest;
import com.ridemate.knowledge.entity.DocumentImage;
import com.ridemate.knowledge.entity.DocumentTable;
import com.ridemate.knowledge.entity.KnowledgeDocument;
import com.ridemate.knowledge.entity.DocumentVector;
import com.ridemate.knowledge.mapper.DocumentImageMapper;
import com.ridemate.knowledge.mapper.DocumentTableMapper;
import com.ridemate.knowledge.mapper.KnowledgeDocumentMapper;
import com.ridemate.knowledge.model.ParsedDocument;
import com.ridemate.knowledge.service.DocumentParserService;
import com.ridemate.knowledge.service.KnowledgeService;
import com.ridemate.knowledge.service.VectorService;
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

    @Autowired
    private VectorService vectorService;

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

        // 删除文档相关的向量
        vectorService.deleteVectorsByDocumentId(id);
        
        knowledgeDocumentMapper.deleteById(id);
        logger.info("文档删除成功: ID={}", id);
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        logger.info("智能问答: 问题={}, 用户ID={}, 会话ID={}", request.getQuestion(), request.getUserId(), request.getSessionId());
        try {
            // 1. 向量检索相关文档
            List<DocumentVector> similarVectors = vectorService.searchSimilarVectors(request.getQuestion(), request.getTopK());
            logger.info("检索到 {} 个相关文档片段", similarVectors.size());

            // 2. 构建上下文
            StringBuilder context = new StringBuilder();
            for (DocumentVector vector : similarVectors) {
                context.append(vector.getContent()).append("\n\n");
            }

            // 3. 调用大模型生成回答
            String answer = generateAnswer(request.getQuestion(), context.toString());

            // 4. 构建响应
            ChatResponse response = new ChatResponse();
            response.setQuestion(request.getQuestion());
            response.setAnswer(answer);
            response.setSessionId(request.getSessionId() != null ? request.getSessionId() : generateSessionId());
            response.setTimestamp(java.time.LocalDateTime.now());

            // 5. 添加引用文档
            List<ChatResponse.ReferenceDocument> references = new ArrayList<>();
            for (DocumentVector vector : similarVectors) {
                KnowledgeDocument document = knowledgeDocumentMapper.selectById(vector.getDocumentId());
                if (document != null) {
                    ChatResponse.ReferenceDocument ref = new ChatResponse.ReferenceDocument();
                    ref.setDocumentId(document.getId());
                    ref.setDocumentTitle(document.getTitle());
                    ref.setContent(vector.getContent());
                    // 相似度分数需要根据实际向量计算，这里暂时设为1.0
                    ref.setSimilarityScore(1.0);
                    references.add(ref);
                }
            }
            response.setReferences(references);

            logger.info("智能问答完成: 回答长度={}", answer.length());
            return response;
        } catch (Exception e) {
            logger.error("智能问答失败: {}", e.getMessage(), e);
            throw new RuntimeException("智能问答失败: " + e.getMessage());
        }
    }

    /**
     * 调用大模型生成回答
     * @param question 问题
     * @param context 上下文
     * @return 回答
     */
    private String generateAnswer(String question, String context) {
        logger.info("调用大模型生成回答: 问题长度={}, 上下文长度={}", question.length(), context.length());
        
        // 这里需要集成阿里云通义千问，暂时返回一个示例回答
        // 实际实现时需要使用Spring AI的ChatClient
        String prompt = String.format(
            "基于以下上下文回答用户的问题：\n\n上下文：%s\n\n用户问题：%s\n\n要求：\n1. 仅基于提供的上下文回答\n2. 回答要准确、简洁\n3. 如果上下文没有相关信息，回答'我没有找到相关信息'",
            context, question
        );
        
        // 暂时返回一个模拟回答
        return "基于提供的上下文，我的回答是：这是一个示例回答。实际实现时会调用阿里云通义千问生成真实回答。";
    }

    /**
     * 生成会话ID
     * @return 会话ID
     */
    private String generateSessionId() {
        return java.util.UUID.randomUUID().toString();
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