-- ============================================
-- СХЕМА ДЛЯ MYSQL
-- ============================================

-- Удаляем таблицы, если они существуют
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS users;

-- Создаем таблицу пользователей
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Создаем таблицу сообщений
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_messages_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Вставляем тестовых пользователей
INSERT INTO users (username) VALUES 
    ('Admin'),
    ('Anonymous'),
    ('Tester');

-- Вставляем тестовое сообщение
INSERT INTO messages (content, user_id) 
SELECT 'Hello from MySQL!', u.id 
FROM users u WHERE u.username = 'Admin';