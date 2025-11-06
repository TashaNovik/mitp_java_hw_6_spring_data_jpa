package com.example.hellospring.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.hellospring.model.Message;
import com.example.hellospring.model.User;

@Repository
public class JdbcMessageRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Message> messageRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));

        Message message = new Message();
        message.setId(rs.getLong("msg_id"));
        message.setContent(rs.getString("content"));
        message.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        message.setAuthor(user);
        return message;
    };

    public JdbcMessageRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public Message save(Message message) {
        String sql = "INSERT INTO messages (content, user_id) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, message.getContent());
            ps.setLong(2, message.getAuthor().getId());
            return ps;
        }, keyHolder);
              
        if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("ID")) {
            Number key = (Number) keyHolder.getKeys().get("ID");
            if (key != null) {
                message.setId(key.longValue());
            }
        }
        return message;
    }

    public void saveAll(List<Message> messages) {
        String sql = "INSERT INTO messages (content, user_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, messages, 100, (ps, message) -> {
            ps.setString(1, message.getContent());
            ps.setLong(2, message.getAuthor().getId());
        });
    }

    private final String BASE_SELECT_SQL = "SELECT m.id as msg_id, m.content, m.created_at, m.user_id, u.username FROM messages m JOIN users u ON m.user_id = u.id ";

    public Optional<Message> findById(Long id) {
        String sql = BASE_SELECT_SQL + "WHERE m.id = ?";
        return jdbcTemplate.query(sql, messageRowMapper, id).stream().findFirst();
    }

    public List<Message> findAllPaginated(int page, int size) {
        int offset = page * size;
        String sql = BASE_SELECT_SQL + "ORDER BY m.created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, messageRowMapper, size, offset);
    }

    public List<Message> findByAuthorUsername(String username) {
        String sql = BASE_SELECT_SQL + "WHERE u.username = ? ORDER BY m.created_at DESC";
        return jdbcTemplate.query(sql, messageRowMapper, username);
    }
}
