package com.example.hellospring.repository;

import com.example.hellospring.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Optional;

@Repository
public class JdbcUserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        return user;
    };

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username FROM users WHERE username = ?";
        return jdbcTemplate.query(sql, userRowMapper, username).stream().findFirst();
    }
    
    public User save(User user) {
        String sql = "INSERT INTO users (username) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            return ps;
        }, keyHolder);
        
        // Безопасное извлечение сгенерированного ID из множественных ключей
        if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("ID")) {
            Number key = (Number) keyHolder.getKeys().get("ID");
            if (key != null) {
                user.setId(key.longValue());
            }
        }
        return user;
    }
}