package com.ridemate.knowledge.mapper;

import com.ridemate.knowledge.entity.DocumentVector;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文档向量Mapper接口
 */
@Mapper
public interface DocumentVectorMapper {
    /**
     * 插入文档向量
     * @param vector 文档向量实体
     * @return 影响行数
     */
    int insert(DocumentVector vector);

    /**
     * 根据文档ID获取向量
     * @param documentId 文档ID
     * @return 文档向量列表
     */
    List<DocumentVector> selectByDocumentId(Long documentId);

    /**
     * 根据文档ID删除向量
     * @param documentId 文档ID
     * @return 影响行数
     */
    int deleteByDocumentId(Long documentId);

    /**
     * 向量检索
     * @param vector 查询向量
     * @param limit 限制条数
     * @return 相似向量列表
     */
    List<DocumentVector> searchVectors(@Param("vector") float[] vector, @Param("limit") int limit);
}