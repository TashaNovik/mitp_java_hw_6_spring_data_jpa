-- ============================================
-- СХЕМА ДЛЯ POSTGRESQL
-- ============================================

-- Удаляем таблицы, если они существуют
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Создаем таблицу пользователей
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создаем таблицу сообщений
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_messages_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Создаем индексы для оптимизации
CREATE INDEX idx_messages_user_id ON messages(user_id);
CREATE INDEX idx_messages_created_at ON messages(created_at DESC);
CREATE INDEX idx_users_username ON users(username);

-- Вставляем тестовых пользователей
INSERT INTO users (username) VALUES 
    ('Admin'),
    ('Anonymous'),
    ('Tester');

-- Вставляем тестовое сообщение
INSERT INTO messages (content, user_id) 
SELECT 'Hello from PostgreSQL!', u.id 
FROM users u WHERE u.username = 'Admin';