-- 创建扩展（如果不存在）
CREATE EXTENSION IF NOT EXISTS vector;

-- 文档向量表
CREATE TABLE IF NOT EXISTS document_vector (
    id BIGINT PRIMARY KEY,
    content TEXT NOT NULL COMMENT '文档内容',
    vector VECTOR(1536) NOT NULL COMMENT '文档向量（通义千问embedding维度为1536）',
    category VARCHAR(50) NOT NULL COMMENT '文档分类',
    FOREIGN KEY (id) REFERENCES knowledge_document(id) ON DELETE CASCADE
);

-- 创建向量索引（提高检索性能）
CREATE INDEX IF NOT EXISTS idx_document_vector ON document_vector USING ivfflat (vector vector_cosine_ops) WITH (lists = 100);
