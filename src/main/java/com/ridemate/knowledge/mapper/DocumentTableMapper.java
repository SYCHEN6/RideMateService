package com.ridemate.knowledge.mapper;

import com.ridemate.knowledge.entity.DocumentTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文档表格Mapper接口
 */
@Mapper
public interface DocumentTableMapper {
    /**
     * 插入文档表格
     * @param table 文档表格实体
     * @return 受影响的行数
     */
    int insert(DocumentTable table);

    /**
     * 根据文档ID查询表格列表
     * @param documentId 文档ID
     * @return 表格列表
     */
    List<DocumentTable> selectByDocumentId(@Param("documentId") Long documentId);

    /**
     * 根据文档ID删除表格
     * @param documentId 文档ID
     * @return 受影响的行数
     */
    int deleteByDocumentId(@Param("documentId") Long documentId);
}
