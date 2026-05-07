package com.ridemate.knowledge.mapper;

import com.ridemate.knowledge.entity.DocumentImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文档图片Mapper接口
 */
@Mapper
public interface DocumentImageMapper {
    /**
     * 插入文档图片
     * @param image 文档图片实体
     * @return 受影响的行数
     */
    int insert(DocumentImage image);

    /**
     * 根据文档ID查询图片列表
     * @param documentId 文档ID
     * @return 图片列表
     */
    List<DocumentImage> selectByDocumentId(@Param("documentId") Long documentId);

    /**
     * 根据文档ID删除图片
     * @param documentId 文档ID
     * @return 受影响的行数
     */
    int deleteByDocumentId(@Param("documentId") Long documentId);
}
