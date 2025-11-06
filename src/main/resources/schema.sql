-- ============================================
-- СХЕМА ДЛЯ H2 DATABASE (по умолчанию)
-- ============================================

-- Удаляем таблицы, если они существуют, для чистого старта
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS users;

-- Создаем таблицу пользователей
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создаем таблицу сообщений
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) -- Внешний ключ, связывающий с таблицей users
);

-- Создаем индексы для оптимизации
CREATE INDEX idx_messages_user_id ON messages(user_id);
CREATE INDEX idx_messages_created_at ON messages(created_at DESC);

-- Вставляем тестовых пользователей
INSERT INTO users (username) VALUES 
    ('Admin'),
    ('Anonymous'),
    ('Tester');

-- Вставляем тестовое сообщение
INSERT INTO messages (content, user_id) 
SELECT 'Hello from H2!', u.id 
FROM users u WHERE u.username = 'Admin';