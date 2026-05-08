package com.ridemate.knowledge.controller;

import com.ridemate.knowledge.dto.ChatRequest;
import com.ridemate.knowledge.dto.ChatResponse;
import com.ridemate.knowledge.dto.KnowledgeDocumentResponse;
import com.ridemate.knowledge.dto.UploadDocumentRequest;
import com.ridemate.knowledge.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 知识库控制器
 */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;

    /**
     * 上传文档
     * @param file 上传的文件
     * @param title 文档标题
     * @param category 文档分类
     * @return 文档响应
     */
    @PostMapping("/documents")
    public ResponseEntity<KnowledgeDocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("category") String category) {
        
        UploadDocumentRequest request = new UploadDocumentRequest();
        request.setTitle(title);
        request.setCategory(category);
        
        KnowledgeDocumentResponse document = knowledgeService.uploadDocument(file, request);
        return new ResponseEntity<>(document, HttpStatus.CREATED);
    }

    /**
     * 根据ID获取文档
     * @param id 文档ID
     * @return 文档响应
     */
    @GetMapping("/documents/{id}")
    public ResponseEntity<KnowledgeDocumentResponse> getDocumentById(@PathVariable Long id) {
        try {
            KnowledgeDocumentResponse document = knowledgeService.getDocumentById(id);
            return ResponseEntity.ok(document);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取所有文档
     * @return 文档响应列表
     */
    @GetMapping("/documents")
    public ResponseEntity<List<KnowledgeDocumentResponse>> getAllDocuments() {
        List<KnowledgeDocumentResponse> documents = knowledgeService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    /**
     * 根据分类获取文档
     * @param category 文档分类
     * @return 文档响应列表
     */
    @GetMapping("/documents/category/{category}")
    public ResponseEntity<List<KnowledgeDocumentResponse>> getDocumentsByCategory(@PathVariable String category) {
        List<KnowledgeDocumentResponse> documents = knowledgeService.getDocumentsByCategory(category);
        return ResponseEntity.ok(documents);
    }

    /**
     * 删除文档
     * @param id 文档ID
     * @return 响应
     */
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        try {
            knowledgeService.deleteDocument(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 智能问答
     * @param request 聊天请求
     * @return 聊天响应
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = knowledgeService.chat(request);
        return ResponseEntity.ok(response);
    }
}