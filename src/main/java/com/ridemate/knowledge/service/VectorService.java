package com.ridemate.knowledge.service;

import com.ridemate.knowledge.entity.DocumentVector;

import java.util.List;

/**
 * 向量服务接口
 */
public interface VectorService {
    /**
     * 生成文档内容的向量
     * @param content 文档内容
     * @return 向量数组
     */
    float[] generateVector(String content);

    /**
     * 批量生成文档内容的向量
     * @param contents 文档内容列表
     * @return 向量数组列表
     */
    List<float[]> generateVectors(List<String> contents);

    /**
     * 为文档生成并存储向量
     * @param documentId 文档ID
     * @param content 文档内容
     * @return 生成的向量列表
     */
    List<DocumentVector> generateAndStoreVectors(Long documentId, String content);

    /**
     * 根据查询文本检索相似向量
     * @param query 查询文本
     * @param limit 限制条数
     * @return 相似向量列表
     */
    List<DocumentVector> searchSimilarVectors(String query, int limit);

    /**
     * 删除文档的所有向量
     * @param documentId 文档ID
     */
    void deleteVectorsByDocumentId(Long documentId);
}