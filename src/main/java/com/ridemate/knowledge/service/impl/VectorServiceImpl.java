package com.ridemate.knowledge.service.impl;

import com.ridemate.knowledge.entity.DocumentVector;
import com.ridemate.knowledge.mapper.DocumentVectorMapper;
import com.ridemate.knowledge.service.VectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 向量服务实现类
 */
@Service
public class VectorServiceImpl implements VectorService {

    private static final Logger logger = LoggerFactory.getLogger(VectorServiceImpl.class);

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private DocumentVectorMapper vectorMapper;

    // 文档拆分大小
    private static final int CHUNK_SIZE = 500;

    // 文档拆分重叠大小
    private static final int OVERLAP_SIZE = 50;

    @Override
    public float[] generateVector(String content) {
        logger.info("生成向量，内容长度: {}", content.length());
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(content);
        return embeddingResponse.getResult().getOutput().toArray(new float[0]);
    }

    @Override
    public List<float[]> generateVectors(List<String> contents) {
        logger.info("批量生成向量，文档数量: {}", contents.size());
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(contents);
        return embeddingResponse.getResults().stream()
                .map(result -> result.getOutput().toArray(new float[0]))
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentVector> generateAndStoreVectors(Long documentId, String content) {
        logger.info("为文档生成并存储向量，文档ID: {}", documentId);

        // 1. 拆分文档内容
        List<String> chunks = splitDocument(content, CHUNK_SIZE, OVERLAP_SIZE);
        logger.info("文档拆分为 {} 个片段", chunks.size());

        // 2. 批量生成向量
        List<float[]> vectors = generateVectors(chunks);

        // 3. 存储向量
        List<DocumentVector> documentVectors = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < chunks.size(); i++) {
            DocumentVector vector = new DocumentVector();
            vector.setDocumentId(documentId);
            vector.setContent(chunks.get(i));
            vector.setVector(vectors.get(i));
            vector.setCreateTime(now);
            vector.setUpdateTime(now);

            vectorMapper.insert(vector);
            documentVectors.add(vector);
        }

        logger.info("文档向量存储完成，共存储 {} 个向量", documentVectors.size());
        return documentVectors;
    }

    @Override
    public List<DocumentVector> searchSimilarVectors(String query, int limit) {
        logger.info("根据查询文本检索相似向量: {}, 限制条数: {}", query, limit);

        // 1. 生成查询向量
        float[] queryVector = generateVector(query);

        // 2. 执行向量检索
        List<DocumentVector> similarVectors = vectorMapper.searchVectors(queryVector, limit);
        logger.info("检索到 {} 个相似向量", similarVectors.size());

        return similarVectors;
    }

    @Override
    public void deleteVectorsByDocumentId(Long documentId) {
        logger.info("删除文档的所有向量，文档ID: {}", documentId);
        vectorMapper.deleteByDocumentId(documentId);
        logger.info("文档向量删除完成");
    }

    /**
     * 拆分文档内容
     * @param content 文档内容
     * @param chunkSize 每个片段的大小
     * @param overlapSize 片段重叠大小
     * @return 拆分后的文档片段列表
     */
    private List<String> splitDocument(String content, int chunkSize, int overlapSize) {
        List<String> chunks = new ArrayList<>();
        if (content == null || content.isEmpty()) {
            return chunks;
        }

        int start = 0;
        int length = content.length();

        while (start < length) {
            int end = Math.min(start + chunkSize, length);
            chunks.add(content.substring(start, end));
            start += (chunkSize - overlapSize);
        }

        return chunks;
    }
}