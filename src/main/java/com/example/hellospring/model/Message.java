package com.example.hellospring.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message {
    private Long id;
    private String content;
    private User author; // Связь с пользователем
    private LocalDateTime createdAt;

    public Message() {}
    public Message(String content, User author) { this.content = content; this.author = author; }

    // Геттеры, сеттеры, equals/hashCode
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; Message message = (Message) o; return Objects.equals(id, message.id); }
    @Override
    public int hashCode() { return Objects.hash(id); }
}
