package com.ridemate.knowledge.service;

import com.ridemate.knowledge.dto.KnowledgeDocumentResponse;
import com.ridemate.knowledge.dto.UploadDocumentRequest;
import com.ridemate.knowledge.dto.ChatRequest;
import com.ridemate.knowledge.dto.ChatResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 知识库服务接口
 */
public interface KnowledgeService {
    /**
     * 上传文档
     * @param file 上传的文件
     * @param request 文档请求信息
     * @return 文档响应信息
     */
    KnowledgeDocumentResponse uploadDocument(MultipartFile file, UploadDocumentRequest request);

    /**
     * 根据ID获取文档
     * @param id 文档ID
     * @return 文档响应信息
     */
    KnowledgeDocumentResponse getDocumentById(Long id);

    /**
     * 获取所有文档
     * @return 文档响应列表
     */
    List<KnowledgeDocumentResponse> getAllDocuments();

    /**
     * 根据分类获取文档
     * @param category 文档分类
     * @return 文档响应列表
     */
    List<KnowledgeDocumentResponse> getDocumentsByCategory(String category);

    /**
     * 删除文档
     * @param id 文档ID
     */
    void deleteDocument(Long id);

    /**
     * 智能问答
     * @param request 聊天请求
     * @return 聊天响应
     */
    ChatResponse chat(ChatRequest request);
}