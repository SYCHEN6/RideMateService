# RideMate - 智能骑行社交平台（终极版）

## 项目概述

**项目名称**：RideMate - 智能骑行社交平台
**项目定位**：面向骑行爱好者的智能社交平台，提供路线推荐、伙伴匹配、知识问答等服务
**技术栈**：
- 后端框架：Spring Boot 3.2.5
- Java版本：Java 21
- Spring AI版本：1.1.0
- Spring AI Alibaba版本：1.1.2.0
- AI框架：LangChain4j (Java) + LangChain/LangGraph (Python)
- ORM框架：MyBatis 3.5.13
- 向量数据库：pgvector (PostgreSQL向量扩展)
- 语言模型：阿里云通义千问
- 关系数据库：MySQL 8.x
- 文件存储：MinIO
- 缓存：Redis
- API网关：Nginx
- 前端框架：React 18 (响应式设计)
- 文档解析：Apache Tika + PDFBox + POI

**项目架构**：前后端分离架构

## 功能模块优先级

### 第一阶段：核心功能验证（优先级：★★★★★）

#### 1. 骑行知识库（RAG核心应用）- 高优先级
- 骑行装备知识库
- 骑行安全指南
- 维修保养知识
- 智能问答系统（基于RAG）
- 复杂文档解析（支持图片、表格、PDF、Word等）

#### 2. 基础路线管理 - 高优先级
- 路线上传与分享
- 路线搜索与浏览
- 路线详情查看

#### 3. 简化用户系统 - 低优先级
- 匿名使用支持
- 简单用户标识（可选登录）

### 第二阶段：智能增强功能（优先级：★★★★☆）

#### 4. AI路线优化
- 基于用户偏好的路线推荐
- 考虑坡度、路况、风景等因素的路线优化
- 路线难度评估

#### 5. 骑行搭子匹配
- 基于路线、时间、兴趣的伙伴匹配
- 匹配算法实现

### 第三阶段：扩展功能（优先级：★★★☆☆）

#### 6. 行程规划
- 多日骑行行程规划
- 补给点、休息点推荐

#### 7. 骑行记录与分享
- 骑行数据记录（距离、时间、速度等）
- 轨迹可视化

## 技术架构设计

### 1. 系统架构图

```
┌─────────────────────────────────────────────────────────┐
│                     前端应用层                          │
│  React 18 (响应式设计)                                  │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                     API网关层                          │
│  Nginx                                                  │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                     后端服务层                          │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐│
│  │   知识库服务  │  │   路线服务    │  │   用户服务    ││
│  │ (Spring Boot) │  │ (Spring Boot) │  │ (Spring Boot) ││
│  └───────────────┘  └───────────────┘  └───────────────┘│
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────┼───────────────────────────────┐
│                         │                               │
▼                         ▼                               ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│   AI服务层      │ │   AI服务层      │ │   AI服务层      │
│  LangChain4j    │ │  LangChain      │ │  LangGraph      │
│   (Java)        │ │   (Python)      │ │   (Python)      │
└─────────────────┘ └─────────────────┘ └─────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                     数据存储层                          │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐│
│  │   MySQL       │  │   PostgreSQL  │  │   MinIO       ││
│  │   (关系数据)  │  │   (pgvector)  │  │   (文件存储)  ││
│  └───────────────┘  └───────────────┘  └───────────────┘│
│  ┌───────────────┐                                      │
│  │   Redis       │                                      │
│  │   (缓存)      │                                      │
│  └───────────────┘                                      │
└─────────────────────────────────────────────────────────┘
```

### 2. 核心技术选型调整说明

#### Spring AI 1.1.0 & Spring AI Alibaba 1.1.2.0
- **选择理由**：
  - 最新稳定版本，提供更多功能和改进
  - 更好的阿里云通义千问集成
  - 增强的向量存储支持
  - 改进的文档处理能力

#### MyBatis 3.5.13
- **选择理由**：
  - 轻量级ORM框架，性能优秀
  - SQL语句与代码分离，便于维护
  - 强大的动态SQL功能
  - 丰富的插件生态
  - 支持存储过程和高级映射

#### Nginx作为API网关
- **选择理由**：
  - 高性能的HTTP和反向代理服务器
  - 强大的负载均衡能力
  - 灵活的请求路由和重写规则
  - 优秀的静态资源处理能力
  - 丰富的模块生态

#### LangChain4j + LangChain/LangGraph
- **选择理由**：
  - **LangChain4j**：Java生态的LLM应用框架，与Spring Boot集成良好
  - **LangChain**：Python生态的LLM应用框架，功能丰富，社区活跃
  - **LangGraph**：Python生态的智能体工作流框架，适合复杂AI逻辑
  - 混合开发：充分利用Java的稳定性和Python的AI生态优势

### 3. Java与Python混合开发架构

```
┌─────────────────────────┐    ┌─────────────────────────┐
│   Java后端服务         │    │   Python AI服务        │
├─────────────────────────┤    ├─────────────────────────┤
│  - Spring Boot          │    │  - FastAPI             │
│  - Spring AI 1.1.0      │    │  - LangChain           │
│  - LangChain4j          │    │  - LangGraph           │
│  - MyBatis              │    │  - Transformers        │
└─────────────────────────┘    └─────────────────────────┘
             │                             │
             └─────────────────────┬───────┘
                                   │
                               ┌───▼─────┐
                               │  Nginx  │
                               └───┬─────┘
                                   │
                               ┌───▼─────┐
                               │  前端   │
                               │  React  │
                               └─────────┘
```

#### 职责划分
- **Java服务**：
  - 核心业务逻辑
  - 数据持久化
  - API接口提供
  - 与外部系统集成

- **Python服务**：
  - 复杂RAG逻辑
  - 高级AI模型调用
  - 自然语言处理
  - 机器学习算法

## 第一阶段详细实现计划

### 第1-2周：项目初始化与基础架构搭建

#### 1. 环境准备
- 安装JDK 21
- 安装Maven 3.9+
- 安装MySQL 8.x
- 安装PostgreSQL 15+ (带pgvector扩展)
- 安装MinIO
- 安装Redis
- 安装Nginx
- 安装Node.js 18+ 和 npm 9+
- 安装Python 3.10+ 和 pip

#### 2. 项目结构搭建
```
ridemate/
├── ridemate-backend/           # Java 21后端主项目
│   ├── ridemate-common/        # 公共组件模块
│   │   ├── src/main/java/com/ridemate/common/
│   │   ├── src/main/resources/
│   │   └── pom.xml
│   ├── ridemate-knowledge/     # 知识库服务模块
│   │   ├── src/main/java/com/ridemate/knowledge/
│   │   ├── src/main/resources/
│   │   │   ├── mapper/         # MyBatis映射文件
│   │   │   └── application.yml
│   │   └── pom.xml
│   ├── ridemate-route/         # 路线服务模块
│   │   ├── src/main/java/com/ridemate/route/
│   │   ├── src/main/resources/
│   │   │   ├── mapper/         # MyBatis映射文件
│   │   │   └── application.yml
│   │   └── pom.xml
│   ├── ridemate-user/          # 用户服务模块
│   │   ├── src/main/java/com/ridemate/user/
│   │   ├── src/main/resources/
│   │   │   ├── mapper/         # MyBatis映射文件
│   │   │   └── application.yml
│   │   └── pom.xml
│   └── pom.xml                 # 父项目pom
├── ridemate-ai/                # Python AI服务
│   ├── rag-service/            # RAG服务
│   │   ├── app.py              # FastAPI应用
│   │   ├── requirements.txt    # 依赖列表
│   │   └── src/                # 源代码
│   └── recommendation-service/ # 推荐服务
│       ├── app.py              # FastAPI应用
│       ├── requirements.txt    # 依赖列表
│       └── src/                # 源代码
├── ridemate-frontend/          # React前端项目
│   ├── src/
│   │   ├── components/         # 通用组件
│   │   ├── pages/              # 页面组件
│   │   ├── services/           # API服务
│   │   ├── hooks/              # 自定义Hooks
│   │   ├── utils/              # 工具函数
│   │   └── styles/             # 样式文件
│   ├── public/
│   └── package.json
├── nginx/                      # Nginx配置
│   └── nginx.conf
└── docker-compose.yml          # Docker部署配置
```

#### 3. 数据库初始化
- 创建MySQL数据库和表结构
- 创建PostgreSQL数据库并启用pgvector扩展
- 创建MinIO存储桶
- 配置Redis

#### 4. Nginx配置
```nginx
# nginx.conf

user  nginx;
worker_processes  1;

error_log  /var/log/nginx/error.log warn;
pid        /var/run/nginx.pid;

events {
    worker_connections  1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    keepalive_timeout  65;

    # 前端服务
    server {
        listen       80;
        server_name  localhost;

        location / {
            root   /usr/share/nginx/html;
            index  index.html index.htm;
            try_files $uri $uri/ /index.html;
        }

        # API网关路由
        location /api/knowledge/ {
            proxy_pass http://knowledge-service:8080/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /api/routes/ {
            proxy_pass http://route-service:8081/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /api/users/ {
            proxy_pass http://user-service:8082/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /api/ai/ {
            proxy_pass http://ai-service:8000/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   /usr/share/nginx/html;
        }
    }
}
```

### 第3-5周：骑行知识库（RAG核心）实现

#### 1. MyBatis配置
```yaml
# application.yml

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ridemate?useSSL=false&serverTimezone=UTC
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.ridemate.knowledge.entity
  configuration:
    map-underscore-to-camel-case: true
```

#### 2. MyBatis映射文件示例
```xml
<!-- KnowledgeDocumentMapper.xml -->

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.ridemate.knowledge.mapper.KnowledgeDocumentMapper">
    
    <resultMap id="KnowledgeDocumentResultMap" type="com.ridemate.knowledge.entity.KnowledgeDocument">
        <id property="id" column="id" />
        <result property="title" column="title" />
        <result property="content" column="content" />
        <result property="category" column="category" />
        <result property="source" column="source" />
        <result property="fileType" column="file_type" />
        <result property="metadata" column="metadata" typeHandler="org.apache.ibatis.type.JsonTypeHandler" />
        <result property="createTime" column="create_time" />
        <result property="updateTime" column="update_time" />
    </resultMap>
    
    <insert id="insert" parameterType="com.ridemate.knowledge.entity.KnowledgeDocument">
        INSERT INTO knowledge_document (title, content, category, source, file_type, metadata)
        VALUES (#{title}, #{content}, #{category}, #{source}, #{fileType}, #{metadata})
    </insert>
    
    <select id="selectById" parameterType="java.lang.Long" resultMap="KnowledgeDocumentResultMap">
        SELECT * FROM knowledge_document WHERE id = #{id}
    </select>
    
    <select id="selectAll" resultMap="KnowledgeDocumentResultMap">
        SELECT * FROM knowledge_document ORDER BY create_time DESC
    </select>
    
    <delete id="deleteById" parameterType="java.lang.Long">
        DELETE FROM knowledge_document WHERE id = #{id}
    </delete>
</mapper>
```

#### 3. Java服务层实现（使用LangChain4j）
```java
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
    
    private void generateVectorAsync(KnowledgeDocument document) {
        CompletableFuture.runAsync(() -> {
            try {
                restTemplate.postForEntity(
                    AI_SERVICE_URL + "/generate-vector", 
                    new VectorGenerationRequest(document.getId(), document.getContent()), 
                    Void.class
                );
            } catch (Exception e) {
                log.error("Failed to generate vector for document {}", document.getId(), e);
            }
        });
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

#### 4. Python RAG服务实现（使用LangChain）
```python
# ridemate-ai/rag-service/app.py

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

#### 5. API接口实现
- `POST /api/knowledge/documents` - 上传文档
- `GET /api/knowledge/documents` - 获取文档列表
- `GET /api/knowledge/documents/{id}` - 获取文档详情
- `DELETE /api/knowledge/documents/{id}` - 删除文档
- `POST /api/knowledge/query` - 智能问答

### 第6-7周：基础路线管理实现

#### 1. MyBatis映射文件
```xml
<!-- RouteMapper.xml -->

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.ridemate.route.mapper.RouteMapper">
    
    <resultMap id="RouteResultMap" type="com.ridemate.route.entity.Route">
        <id property="id" column="id" />
        <result property="name" column="name" />
        <result property="description" column="description" />
        <result property="startPoint" column="start_point" />
        <result property="endPoint" column="end_point" />
        <result property="distance" column="distance" />
        <result property="duration" column="duration" />
        <result property="difficulty" column="difficulty" />
        <result property="elevationGain" column="elevation_gain" />
        <result property="creatorId" column="creator_id" />
        <result property="createTime" column="create_time" />
        <result property="updateTime" column="update_time" />
        <collection property="details" ofType="com.ridemate.route.entity.RouteDetail" column="id" select="selectDetailsByRouteId" />
    </resultMap>
    
    <insert id="insert" parameterType="com.ridemate.route.entity.Route">
        INSERT INTO route (name, description, start_point, end_point, distance, duration, difficulty, elevation_gain, creator_id)
        VALUES (#{name}, #{description}, #{startPoint}, #{endPoint}, #{distance}, #{duration}, #{difficulty}, #{elevationGain}, #{creatorId})
    </insert>
    
    <select id="selectById" parameterType="java.lang.Long" resultMap="RouteResultMap">
        SELECT * FROM route WHERE id = #{id}
    </select>
    
    <select id="selectAll" resultMap="RouteResultMap">
        SELECT * FROM route ORDER BY create_time DESC
    </select>
    
    <select id="selectDetailsByRouteId" parameterType="java.lang.Long" resultType="com.ridemate.route.entity.RouteDetail">
        SELECT * FROM route_detail WHERE route_id = #{routeId} ORDER BY sequence
    </select>
    
    <!-- 其他SQL语句 -->
</mapper>
```

#### 2. 路线服务实现
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

#### 3. API接口实现
- `POST /api/routes` - 创建路线
- `GET /api/routes` - 获取路线列表
- `GET /api/routes/{id}` - 获取路线详情
- `PUT /api/routes/{id}` - 更新路线
- `DELETE /api/routes/{id}` - 删除路线

### 第8-9周：简化用户系统实现

#### 1. MyBatis映射文件
```xml
<!-- UserMapper.xml -->

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.ridemate.user.mapper.UserMapper">
    
    <resultMap id="UserResultMap" type="com.ridemate.user.entity.User">
        <id property="id" column="id" />
        <result property="userIdentifier" column="user_identifier" />
        <result property="nickname" column="nickname" />
        <result property="avatar" column="avatar" />
        <result property="createTime" column="create_time" />
    </resultMap>
    
    <insert id="insert" parameterType="com.ridemate.user.entity.User">
        INSERT INTO user (user_identifier, nickname, avatar)
        VALUES (#{userIdentifier}, #{nickname}, #{avatar})
    </insert>
    
    <select id="selectById" parameterType="java.lang.Long" resultMap="UserResultMap">
        SELECT * FROM user WHERE id = #{id}
    </select>
    
    <select id="selectByIdentifier" parameterType="java.lang.String" resultMap="UserResultMap">
        SELECT * FROM user WHERE user_identifier = #{userIdentifier}
    </select>
    
    <!-- 其他SQL语句 -->
</mapper>
```

#### 2. 用户服务实现
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

#### 3. API接口实现
- `POST /api/users/anonymous` - 创建匿名用户
- `GET /api/users/{id}` - 获取用户信息
- `PUT /api/users/{id}` - 更新用户信息

### 第10-12周：React前端实现

#### 1. 前端项目初始化
```bash
# 使用Vite创建React项目
npm create vite@latest ridemate-frontend -- --template react
cd ridemate-frontend

# 安装依赖
npm install axios react-router-dom styled-components
```

#### 2. API服务封装
```javascript
// src/services/api.js

import axios from 'axios';

const api = axios.create({
    baseURL: '/api',
    timeout: 10000,
});

// 请求拦截器
api.interceptors.request.use(
    (config) => {
        // 添加认证信息等
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 响应拦截器
api.interceptors.response.use(
    (response) => {
        return response.data;
    },
    (error) => {
        console.error('API Error:', error);
        return Promise.reject(error);
    }
);

export default api;
```

```javascript
// src/services/knowledgeApi.js

import api from './api';

export const knowledgeApi = {
    uploadDocument: (file, category) => {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('category', category);
        return api.post('/knowledge/documents', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    },
    
    getDocuments: () => {
        return api.get('/knowledge/documents');
    },
    
    getDocumentById: (id) => {
        return api.get(`/knowledge/documents/${id}`);
    },
    
    deleteDocument: (id) => {
        return api.delete(`/knowledge/documents/${id}`);
    },
    
    query: (question) => {
        return api.post('/knowledge/query', { question });
    },
};
```

#### 3. 关键页面实现

**知识库页面**：
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

## 部署与测试计划

### 1. 本地开发环境
- 使用Docker容器化部署所有服务
- 使用docker-compose管理服务间依赖
- 编写单元测试和集成测试
- API接口测试使用Postman
- 前端测试使用Jest和React Testing Library

### 2. Docker Compose配置
```yaml
# docker-compose.yml

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

## 项目亮点

1. **技术栈前沿**：
   - Java 21 + Spring Boot 3.2.5
   - Spring AI 1.1.0 + Spring AI Alibaba 1.1.2.0
   - LangChain4j + LangChain/LangGraph 混合开发
   - React 18 响应式设计

2. **架构设计先进**：
   - 前后端分离架构
   - 微服务模块化设计
   - Java与Python混合开发，充分发挥各自优势
   - Nginx作为API网关，提供高性能路由

3. **功能强大**：
   - 复杂文档解析（支持PDF、Word、图片、表格等）
   - 基于RAG的智能问答系统
   - 灵活的路线管理功能
   - 响应式设计，适配各种设备

4. **开发效率高**：
   - MyBatis简化数据库操作
   - Docker容器化部署，环境一致性保障
   - 成熟的开发框架和工具链

5. **可扩展性强**：
   - 模块化设计，便于后续功能扩展
   - 支持多种文档格式和数据类型
   - 易于集成新的AI模型和算法

## 总结

终极版的RideMate智能骑行社交平台项目规划已经完全按照您的要求进行了调整：
- 更新Spring AI版本到1.1.0，Spring AI Alibaba版本到1.1.2.0
- 添加MyBatis支持
- 使用Nginx作为API网关
- 集成LangChain4j框架
- 规划了Java和Python混合开发的架构
- 明确了前后端分离的架构设计
- 提供了详细的实现代码示例

这个项目既展示了前沿的AI技术应用，又具有实际的业务价值，技术栈丰富且先进，非常适合作为简历上的实战项目。项目采用了企业级的架构设计，同时保持了良好的可扩展性和维护性。