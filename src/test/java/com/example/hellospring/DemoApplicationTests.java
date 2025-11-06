package com.example.hellospring;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Перед каждым тестом создаем схему и подготавливаем тестовые данные
    @BeforeEach
    void setup() {
        // Создаем схему для тестов
        jdbcTemplate.execute("DROP TABLE IF EXISTS messages");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users");
        
        jdbcTemplate.execute("""
            CREATE TABLE users (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(100) NOT NULL UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);
        
        jdbcTemplate.execute("""
            CREATE TABLE messages (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                content VARCHAR(255) NOT NULL,
                user_id BIGINT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """);
        
        // Вставляем тестовых пользователей
        jdbcTemplate.execute("INSERT INTO users (id, username) VALUES (1, 'Tester'), (2, 'Anonymous')");
        jdbcTemplate.execute("INSERT INTO messages (content, user_id) VALUES ('Initial message', 1)");
    }

    @Test
    void getMessageById_whenExists_returnsMessage() throws Exception {
        mockMvc.perform(get("/messages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Initial message")))
                .andExpect(jsonPath("$.author.username", is("Tester")));
    }

    @Test
    void getMessageById_whenNotExists_returns404() throws Exception {
        mockMvc.perform(get("/messages/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postMessage_createsNewMessage() throws Exception {
        String jsonPayload = "{\"content\":\"New message\", \"author\":{\"id\":1}}";
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.author.username", is("Tester")));
    }

    @Test
    void getMessagesWithPagination_returnsCorrectPage() throws Exception {
        // В базе уже есть одно сообщение
        mockMvc.perform(get("/messages?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content", is("Initial message")));

        mockMvc.perform(get("/messages?page=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0))); // На второй странице пусто
    }
}