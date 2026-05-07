# RideMateService - 智能骑行社交平台后端

## 项目概述

**RideMate** 是一个面向骑行爱好者的智能社交平台，提供路线推荐、伙伴匹配、知识问答等服务。本项目是RideMate的后端服务部分，基于Spring Boot 3.2.5和Java 21开发。

## 技术栈

- **后端框架**：Spring Boot 3.2.5
- **Java版本**：Java 21
- **Spring AI版本**：1.1.0
- **Spring AI Alibaba版本**：1.1.2.0
- **AI框架**：LangChain4j (Java)
- **ORM框架**：MyBatis 3.5.13
- **向量数据库**：pgvector (PostgreSQL向量扩展)
- **语言模型**：阿里云通义千问
- **关系数据库**：MySQL 8.x
- **文件存储**：MinIO
- **缓存**：Redis
- **文档解析**：Apache Tika + PDFBox + POI

## 功能模块

### 核心功能

#### 1. 骑行知识库（RAG核心应用）
- 骑行装备知识库
- 骑行安全指南
- 维修保养知识
- 智能问答系统（基于RAG）
- 复杂文档解析（支持图片、表格、PDF、Word等）

#### 2. 基础路线管理
- 路线上传与分享
- 路线搜索与浏览
- 路线详情查看

#### 3. 简化用户系统
- 匿名使用支持
- 简单用户标识（可选登录）

### 计划功能

#### 4. AI路线优化
- 基于用户偏好的路线推荐
- 考虑坡度、路况、风景等因素的路线优化
- 路线难度评估

#### 5. 骑行搭子匹配
- 基于路线、时间、兴趣的伙伴匹配
- 匹配算法实现

## 项目结构

```
RideMateService/
├── src/
│   ├── main/
│   │   ├── java/com/ridemate/
│   │   │   ├── knowledge/          # 知识库模块
│   │   │   │   ├── dto/           # 数据传输对象
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   ├── mapper/        # 数据访问层
│   │   │   │   ├── model/         # 业务模型
│   │   │   │   └── service/       # 业务逻辑层
│   │   │   ├── user/              # 用户模块
│   │   │   │   ├── controller/    # 控制器
│   │   │   │   ├── dto/           # 数据传输对象
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   ├── mapper/        # 数据访问层
│   │   │   │   └── service/       # 业务逻辑层
│   │   │   └── RideMateApplication.java  # 应用入口
│   │   └── resources/
│   │       ├── db/                # 数据库脚本
│   │       ├── mapper/            # MyBatis映射文件
│   │       └── application.yml    # 应用配置
│   └── test/                      # 测试代码
├── docs/                          # 项目文档
├── pom.xml                        # Maven配置
└── README.md                      # 项目说明
```

## 环境要求

- JDK 21
- Maven 3.9+
- MySQL 8.x
- PostgreSQL 15+ (带pgvector扩展)
- MinIO
- Redis

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/SYCHEN6/RideMateService.git
cd RideMateService
```

### 2. 配置数据库

1. 创建MySQL数据库：
```sql
CREATE DATABASE ridemate DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库脚本：
```bash
mysql -u username -p ridemate < src/main/resources/db/schema.sql
```

3. 创建PostgreSQL数据库并安装pgvector扩展：
```sql
CREATE DATABASE ridemate_vector;
\c ridemate_vector;
CREATE EXTENSION IF NOT EXISTS vector;
```

4. 执行pgvector数据库脚本：
```bash
psql -U username -d ridemate_vector -f src/main/resources/db/pgvector_schema.sql
```

### 3. 配置应用

修改`src/main/resources/application.yml`文件，配置数据库连接、MinIO、Redis等信息。

### 4. 构建与运行

```bash
# 构建项目
mvn clean install

# 运行项目
mvn spring-boot:run
```

应用将在`http://localhost:8080`启动。

## 开发指南

### 代码规范

- 遵循Spring MVC分层架构
- 实体类使用Lombok的@Data注解
- 使用SLF4J进行日志记录
- 文档解析使用抽象工厂模式

### 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify
```

## 构建与部署

### 构建生产版本

```bash
mvn clean package -DskipTests
```

### Docker部署

```bash
docker build -t ridemate-service .
docker run -p 8080:8080 --name ridemate-service ridemate-service
```

## 许可证

[MIT License](LICENSE)
