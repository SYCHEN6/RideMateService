# RideMate 项目第一阶段实现计划

## 项目概述

本阶段将实现RideMate智能骑行社交平台的核心功能，包括：
- 骑行知识库（RAG核心应用）
- 基础路线管理
- 简化用户系统

## 技术栈

- 后端框架：Spring Boot 3.2.5
- Java版本：Java 21
- Spring AI版本：1.1.0
- Spring AI Alibaba版本：1.1.2.0
- ORM框架：MyBatis 3.5.13
- 向量数据库：pgvector (PostgreSQL向量扩展)
- 语言模型：阿里云通义千问
- 关系数据库：MySQL 8.x
- 文件存储：MinIO
- 缓存：Redis
- API网关：Nginx
- 前端框架：React 18 (响应式设计)
- 文档解析：Apache Tika + PDFBox + POI

## 后端实现

### 1. 项目结构

```
ridemate-backend/
├── ridemate-common/        # 公共组件模块
│   ├── src/main/java/com/ridemate/common/
│   │   ├── exception/      # 统一异常处理
│   │   ├── util/           # 工具类
│   │   └── model/          # 公共数据模型
│   └── pom.xml
├── ridemate-knowledge/     # 知识库服务模块
│   ├── src/main/java/com/ridemate/knowledge/
│   │   ├── controller/     # 控制器
│   │   ├── service/        # 服务层
│   │   ├── mapper/         # MyBatis映射接口
│   │   ├── entity/         # 实体类
│   │   └── dto/            # 数据传输对象
│   ├── src/main/resources/
│   │   ├── mapper/         # MyBatis映射文件
│   │   └── application.yml # 配置文件
│   └── pom.xml
├── ridemate-route/         # 路线服务模块
│   ├── src/main/java/com/ridemate/route/
│   │   ├── controller/     # 控制器
│   │   ├── service/        # 服务层
│   │   ├── mapper/         # MyBatis映射接口
│   │   ├── entity/         # 实体类
│   │   └── dto/            # 数据传输对象
│   ├── src/main/resources/
│   │   ├── mapper/         # MyBatis映射文件
│   │   └── application.yml # 配置文件
│   └── pom.xml
├── ridemate-user/          # 用户服务模块
│   ├── src/main/java/com/ridemate/user/
│   │   ├── controller/     # 控制器
│   │   ├── service/        # 服务层
│   │   ├── mapper/         # MyBatis映射接口
│   │   ├── entity/         # 实体类
│   │   └── dto/            # 数据传输对象
│   ├── src/main/resources/
│   │   ├── mapper/         # MyBatis映射文件
│   │   └── application.yml # 配置文件
│   └── pom.xml
└── pom.xml                 # 父项目pom
```

### 2. 知识库服务实现

#### 2.1 实体类

```java
// KnowledgeDocument.java
@Data
public class KnowledgeDocument {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String source;
    private String fileType;
    private Map<String, Object> metadata;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

// DocumentImage.java
@Data
public class DocumentImage {
    private Long id;
    private Long documentId;
    private String imagePath;
    private String description;
    private Integer pageNumber;
    private LocalDateTime createTime;
}

// DocumentTable.java
@Data
public class DocumentTable {
    private Long id;
    private Long documentId;
    private Map<String, Object> tableData;
    private String description;
    private Integer pageNumber;
    private LocalDateTime createTime;
}
```

#### 2.2 服务层

```java
// DocumentParserService.java
@Service
public class DocumentParserService {
    @Autowired
    private MinioClient minioClient;
    
    public ParsedDocument parseDocument(MultipartFile file) throws Exception {
        ParsedDocument parsedDocument = new ParsedDocument();
        
        // 使用Tika检测文档类型
        Tika tika = new Tika();
        String mimeType = tika.detect(file);
        parsedDocument.setFileType(getFileType(mimeType));
        
        // 根据文档类型选择不同的解析器
        switch (parsedDocument.getFileType()) {
            case PDF -> parsePdf(file, parsedDocument);
            case WORD -> parseWord(file, parsedDocument);
            case IMAGE -> parseImage(file, parsedDocument);
            default -> parseText(file, parsedDocument);
        }
        
        return parsedDocument;
    }
    
    // 各种文档类型的解析方法...
}

// KnowledgeService.java
@Service
public class KnowledgeService {
    @Autowired
    private KnowledgeDocumentMapper documentMapper;
    
    @Autowired
    private DocumentParserService documentParserService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private final String AI_SERVICE_URL = "http://localhost:8000/api/ai/rag";
    
    public KnowledgeDocument uploadDocument(MultipartFile file, String category) throws Exception {
        // 解析文档
        ParsedDocument parsedDocument = documentParserService.parseDocument(file);
        
        // 保存到数据库
        KnowledgeDocument document = new KnowledgeDocument();
        document.setTitle(file.getOriginalFilename());
        document.setContent(parsedDocument.getContent());
        document.setCategory(category);
        document.setFileType(parsedDocument.getFileType().name());
        
        // 构建元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("images", parsedDocument.getImages());
        metadata.put("tables", parsedDocument.getTables());
        document.setMetadata(metadata);
        
        documentMapper.insert(document);
        
        // 异步调用Python服务生成向量
        generateVectorAsync(document);
        
        return document;
    }
    
    public String askQuestion(String question) {
        // 调用Python RAG服务
        RagQueryRequest request = new RagQueryRequest(question);
        ResponseEntity<RagQueryResponse> response = restTemplate.postForEntity(
            AI_SERVICE_URL + "/query", 
            request, 
            RagQueryResponse.class
        );
        
        return response.getBody().getAnswer();
    }
    
    // 其他方法...
}
```

#### 2.3 控制器

```java
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    @Autowired
    private KnowledgeService knowledgeService;
    
    @PostMapping("/documents")
    public ResponseEntity<KnowledgeDocument> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") String category) {
        try {
            KnowledgeDocument document = knowledgeService.uploadDocument(file, category);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/documents")
    public ResponseEntity<List<KnowledgeDocument>> getDocuments() {
        List<KnowledgeDocument> documents = knowledgeService.getDocuments();
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/documents/{id}")
    public ResponseEntity<KnowledgeDocument> getDocumentById(@PathVariable Long id) {
        KnowledgeDocument document = knowledgeService.getDocumentById(id);
        if (document == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(document);
    }
    
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        knowledgeService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/query")
    public ResponseEntity<String> query(@RequestBody QueryRequest request) {
        String answer = knowledgeService.askQuestion(request.getQuestion());
        return ResponseEntity.ok(answer);
    }
}
```

### 3. 路线服务实现

#### 3.1 实体类

```java
// Route.java
@Data
public class Route {
    private Long id;
    private String name;
    private String description;
    private String startPoint;
    private String endPoint;
    private Double distance;
    private Integer duration;
    private String difficulty;
    private Integer elevationGain;
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<RouteDetail> details;
}

// RouteDetail.java
@Data
public class RouteDetail {
    private Long id;
    private Long routeId;
    private Integer sequence;
    private Double latitude;
    private Double longitude;
    private Integer altitude;
    private String name;
}
```

#### 3.2 服务层

```java
@Service
public class RouteService {
    @Autowired
    private RouteMapper routeMapper;
    
    @Autowired
    private RouteDetailMapper routeDetailMapper;
    
    @Autowired
    private MinioClient minioClient;
    
    public Route createRoute(Route route) {
        // 保存路线基本信息
        routeMapper.insert(route);
        
        // 保存路线详情
        if (route.getDetails() != null && !route.getDetails().isEmpty()) {
            for (RouteDetail detail : route.getDetails()) {
                detail.setRouteId(route.getId());
                routeDetailMapper.insert(detail);
            }
        }
        
        return route;
    }
    
    public Route getRouteById(Long id) {
        return routeMapper.selectById(id);
    }
    
    public List<Route> getAllRoutes() {
        return routeMapper.selectAll();
    }
    
    // 其他方法...
}
```

#### 3.3 控制器

```java
@RestController
@RequestMapping("/api/routes")
public class RouteController {
    @Autowired
    private RouteService routeService;
    
    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        Route createdRoute = routeService.createRoute(route);
        return ResponseEntity.ok(createdRoute);
    }
    
    @GetMapping
    public ResponseEntity<List<Route>> getRoutes() {
        List<Route> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        Route route = routeService.getRouteById(id);
        if (route == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(route);
    }
    
    // 其他方法...
}
```

### 4. 用户服务实现

#### 4.1 实体类

```java
// User.java
@Data
public class User {
    private Long id;
    private String userIdentifier;
    private String nickname;
    private String avatar;
    private LocalDateTime createTime;
}
```

#### 4.2 服务层

```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public User createAnonymousUser() {
        User user = new User();
        user.setUserIdentifier(UUID.randomUUID().toString());
        user.setNickname("匿名用户" + RandomStringUtils.randomNumeric(6));
        userMapper.insert(user);
        return user;
    }
    
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
    
    // 其他方法...
}
```

#### 4.3 控制器

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    
    @PostMapping("/anonymous")
    public ResponseEntity<User> createAnonymousUser() {
        User user = userService.createAnonymousUser();
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
    
    // 其他方法...
}
```

## AI服务实现（Python）

### 1. 项目结构

```
ridemate-ai/
├── rag-service/
│   ├── app.py              # FastAPI应用
│   ├── requirements.txt    # 依赖列表
│   └── src/
│       ├── vector_store.py # 向量存储服务
│       ├── rag_chain.py    # RAG链实现
│       └── utils.py        # 工具函数
└── .env                    # 环境变量配置
```

### 2. RAG服务实现

```python
# app.py

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import psycopg2
from langchain_community.vectorstores import PGVector
from langchain_community.embeddings import TongyiEmbeddings
from langchain.prompts import PromptTemplate
from langchain_community.chat_models import TongyiChat
from langchain.schema.runnable import RunnablePassthrough
from langchain.schema.output_parser import StrOutputParser
import json

app = FastAPI(title="RAG Service")

# 配置
CONNECTION_STRING = "postgresql://user:password@localhost:5432/ridemate"
COLLECTION_NAME = "document_vectors"

# 初始化组件
embeddings = TongyiEmbeddings(model="text-embedding-v1")
vector_store = PGVector(
    connection_string=CONNECTION_STRING,
    collection_name=COLLECTION_NAME,
    embedding_function=embeddings
)

chat_model = TongyiChat(model="qwen-plus", temperature=0.7)

# 定义请求和响应模型
class VectorGenerationRequest(BaseModel):
    document_id: int
    content: str

class RagQueryRequest(BaseModel):
    question: str

class RagQueryResponse(BaseModel):
    answer: str

# 定义提示模板
prompt = PromptTemplate(
    template="""
    你是一个骑行专家，请根据提供的上下文回答用户问题。
    上下文包含文档文本、图片描述和表格信息，请综合所有信息进行回答。
    如果上下文信息不足，请明确说明。
    
    上下文：
    {context}
    
    用户问题：
    {question}
    """,
    input_variables=["context", "question"]
)

# 构建RAG链
rag_chain = (
    {"context": RunnablePassthrough.assign(
        docs=lambda x: vector_store.similarity_search(x["question"], k=3)
    ).assign(
        context=lambda x: "\n\n".join([doc.page_content for doc in x["docs"]])
    ), "question": RunnablePassthrough()}
    | prompt
    | chat_model
    | StrOutputParser()
)

@app.post("/api/ai/rag/generate-vector")
async def generate_vector(request: VectorGenerationRequest):
    try:
        # 生成向量并存储
        vector_store.add_texts(
            texts=[request.content],
            metadatas=[{"document_id": request.document_id}]
        )
        return {"status": "success"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/ai/rag/query", response_model=RagQueryResponse)
async def query(request: RagQueryRequest):
    try:
        # 执行RAG查询
        answer = rag_chain.invoke({"question": request.question})
        return RagQueryResponse(answer=answer)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
```

## 前端实现

### 1. 项目结构

```
ridemate-frontend/
├── src/
│   ├── components/         # 通用组件
│   │   ├── Button/
│   │   ├── Input/
│   │   ├── Card/
│   │   ├── Loading/
│   │   └── Select/
│   ├── pages/              # 页面组件
│   │   ├── Home/
│   │   ├── KnowledgeBase/
│   │   ├── Routes/
│   │   └── User/
│   ├── services/           # API服务
│   │   ├── api.js
│   │   ├── knowledgeApi.js
│   │   ├── routeApi.js
│   │   └── userApi.js
│   ├── hooks/              # 自定义Hooks
│   ├── utils/              # 工具函数
│   └── styles/             # 样式文件
├── public/
└── package.json
```

### 2. 关键组件实现

#### 2.1 知识库页面

```jsx
import React, { useState, useEffect } from 'react';
import { knowledgeApi } from '../services/knowledgeApi';
import { Card, Button, Input, Loading, Select } from '../components';

const KnowledgeBasePage = () => {
    const [query, setQuery] = useState('');
    const [answer, setAnswer] = useState('');
    const [loading, setLoading] = useState(false);
    const [documents, setDocuments] = useState([]);
    const [selectedFile, setSelectedFile] = useState(null);
    const [category, setCategory] = useState('equipment');

    useEffect(() => {
        loadDocuments();
    }, []);

    const loadDocuments = async () => {
        try {
            const data = await knowledgeApi.getDocuments();
            setDocuments(data);
        } catch (error) {
            console.error('Failed to load documents:', error);
        }
    };

    const handleQuery = async () => {
        if (!query.trim()) return;
        
        setLoading(true);
        try {
            const data = await knowledgeApi.query(query);
            setAnswer(data.answer);
        } catch (error) {
            console.error('Query failed:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleFileChange = (e) => {
        setSelectedFile(e.target.files[0]);
    };

    const handleUpload = async () => {
        if (!selectedFile) return;
        
        setLoading(true);
        try {
            await knowledgeApi.uploadDocument(selectedFile, category);
            loadDocuments();
            setSelectedFile(null);
        } catch (error) {
            console.error('Upload failed:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="knowledge-base-page">
            <div className="section">
                <h2>智能骑行问答</h2>
                <div className="query-container">
                    <Input
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                        placeholder="输入您的问题..."
                        onKeyPress={(e) => e.key === 'Enter' && handleQuery()}
                    />
                    <Button onClick={handleQuery} disabled={loading}>
                        {loading ? <Loading /> : '提问'}
                    </Button>
                </div>
                {answer && (
                    <div className="answer-card">
                        <h3>回答</h3>
                        <p>{answer}</p>
                    </div>
                )}
            </div>

            <div className="section">
                <h2>文档管理</h2>
                <div className="upload-container">
                    <Input
                        type="file"
                        onChange={handleFileChange}
                        accept=".pdf,.doc,.docx,.jpg,.jpeg,.png"
                        className="file-input"
                    />
                    <Select
                        value={category}
                        onChange={setCategory}
                        options={[
                            { value: 'equipment', label: '骑行装备' },
                            { value: 'safety', label: '骑行安全' },
                            { value: 'maintenance', label: '维修保养' },
                            { value: 'training', label: '骑行训练' },
                        ]}
                    />
                    <Button onClick={handleUpload} disabled={!selectedFile || loading}>
                        {loading ? <Loading /> : '上传'}
                    </Button>
                </div>

                <div className="documents-grid">
                    {documents.map((doc) => (
                        <Card key={doc.id} className="document-card">
                            <h3>{doc.title}</h3>
                            <p className="category">{doc.category}</p>
                            <p className="content-preview">
                                {doc.content.substring(0, 100)}...
                            </p>
                            <Button size="small" onClick={() => handleView(doc.id)}>
                                查看
                            </Button>
                            <Button size="small" variant="danger" onClick={() => handleDelete(doc.id)}>
                                删除
                            </Button>
                        </Card>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default KnowledgeBasePage;
```

## 部署与测试

### 1. Docker Compose配置

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: ridemate
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  postgres:
    image: pgvector/pgvector:pg15
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
      POSTGRES_DB: ridemate
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7.0
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  minio:
    image: minio/minio:latest
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio_data:/data
    command: server --console-address ":9001" /data

  nginx:
    image: nginx:latest
    ports:
      - "80:80"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./ridemate-frontend/build:/usr/share/nginx/html
    depends_on:
      - knowledge-service
      - route-service
      - user-service
      - ai-service

  knowledge-service:
    build:
      context: ./ridemate-backend/ridemate-knowledge
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - postgres
      - minio
      - redis

  route-service:
    build:
      context: ./ridemate-backend/ridemate-route
    ports:
      - "8081:8081"
    depends_on:
      - mysql
      - minio

  user-service:
    build:
      context: ./ridemate-backend/ridemate-user
    ports:
      - "8082:8082"
    depends_on:
      - mysql

  ai-service:
    build:
      context: ./ridemate-ai/rag-service
    ports:
      - "8000:8000"
    depends_on:
      - postgres

volumes:
  mysql_data:
  postgres_data:
  redis_data:
  minio_data:
```

### 2. 启动步骤

1. 启动所有服务：
   ```bash
docker-compose up -d
```

2. 访问前端应用：
   ```
http://localhost
```

3. 测试API接口：
   ```
# 测试文档上传
curl -X POST -F "file=@test.pdf" -F "category=equipment" http://localhost/api/knowledge/documents

# 测试智能问答
curl -X POST -H "Content-Type: application/json" -d '{"question":"如何选择骑行头盔？"}' http://localhost/api/knowledge/query

# 测试路线创建
curl -X POST -H "Content-Type: application/json" -d '{"name":"测试路线","description":"这是一条测试路线","startPoint":"39.9042,116.4074","endPoint":"39.9142,116.4174","distance":10.5,"duration":60,"difficulty":"EASY"}' http://localhost/api/routes

# 测试创建匿名用户
curl -X POST http://localhost/api/users/anonymous
```

## 总结

第一阶段的实现计划涵盖了RideMate智能骑行社交平台的核心功能，包括：
- 骑行知识库（RAG核心应用）
- 基础路线管理
- 简化用户系统

该计划详细说明了后端、AI服务和前端的实现细节，包括代码示例和部署配置。按照这个计划，您可以逐步实现各个功能模块，并通过Docker Compose快速部署和测试整个系统。