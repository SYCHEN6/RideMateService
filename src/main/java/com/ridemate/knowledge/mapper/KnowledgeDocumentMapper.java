package com.ridemate.knowledge.mapper;

import com.ridemate.knowledge.entity.KnowledgeDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识库文档Mapper接口
 */
@Mapper
public interface KnowledgeDocumentMapper {
    /**
     * 插入文档
     * @param document 文档实体
     * @return 受影响的行数
     */
    int insert(KnowledgeDocument document);

    /**
     * 根据ID查询文档
     * @param id 文档ID
     * @return 文档实体
     */
    KnowledgeDocument selectById(@Param("id") Long id);

    /**
     * 查询所有文档
     * @return 文档列表
     */
    List<KnowledgeDocument> selectAll();

    /**
     * 根据分类查询文档
     * @param category 文档分类
     * @return 文档列表
     */
    List<KnowledgeDocument> selectByCategory(@Param("category") String category);

    /**
     * 删除文档
     * @param id 文档ID
     * @return 受影响的行数
     */
    int deleteById(@Param("id") Long id);
}
