-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS ridemate DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ridemate;

-- 用户表（简化版）
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_identifier VARCHAR(100) NOT NULL UNIQUE COMMENT '用户唯一标识',
    nickname VARCHAR(50) COMMENT '用户昵称',
    avatar VARCHAR(255) COMMENT '用户头像',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 知识库文档表
CREATE TABLE IF NOT EXISTS knowledge_document (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '文档标题',
    content TEXT NOT NULL COMMENT '文档内容',
    category VARCHAR(50) NOT NULL COMMENT '文档分类（equipment/safety/maintenance/training）',
    source VARCHAR(100) COMMENT '文档来源',
    file_type VARCHAR(20) COMMENT '文件类型（PDF/WORD/IMAGE/TEXT）',
    metadata JSON COMMENT '文档元数据（包含图片、表格等信息）',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库文档表';

-- 文档图片表
CREATE TABLE IF NOT EXISTS document_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id BIGINT NOT NULL COMMENT '关联的文档ID',
    image_path VARCHAR(255) NOT NULL COMMENT '图片存储路径',
    description TEXT COMMENT '图片描述（OCR识别结果）',
    page_number INT COMMENT '所在页码',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (document_id) REFERENCES knowledge_document(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档图片表';

-- 文档表格表
CREATE TABLE IF NOT EXISTS document_table (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id BIGINT NOT NULL COMMENT '关联的文档ID',
    table_data JSON NOT NULL COMMENT '表格数据',
    description TEXT COMMENT '表格描述',
    page_number INT COMMENT '所在页码',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (document_id) REFERENCES knowledge_document(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档表格表';

-- 路线表
CREATE TABLE IF NOT EXISTS route (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL COMMENT '路线名称',
    description TEXT COMMENT '路线描述',
    start_point VARCHAR(100) NOT NULL COMMENT '起点坐标（格式：lat,lng）',
    end_point VARCHAR(100) NOT NULL COMMENT '终点坐标（格式：lat,lng）',
    distance DOUBLE NOT NULL COMMENT '路线距离（公里）',
    duration INT NOT NULL COMMENT '预计时长（分钟）',
    difficulty VARCHAR(20) NOT NULL COMMENT '难度级别（EASY/MEDIUM/HARD）',
    elevation_gain INT COMMENT '总爬升高度（米）',
    creator_id BIGINT COMMENT '创建者ID（可为空，匿名用户）',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (creator_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路线表';

-- 路线详情表
CREATE TABLE IF NOT EXISTS route_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    route_id BIGINT NOT NULL COMMENT '关联的路线ID',
    sequence INT NOT NULL COMMENT '途经点顺序',
    latitude DOUBLE NOT NULL COMMENT '纬度',
    longitude DOUBLE NOT NULL COMMENT '经度',
    altitude INT COMMENT '海拔高度（米）',
    name VARCHAR(100) COMMENT '途经点名称',
    FOREIGN KEY (route_id) REFERENCES route(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路线详情表';
