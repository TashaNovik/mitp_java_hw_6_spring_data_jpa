package com.example.hellospring.dto;

import java.time.LocalDateTime;

import com.example.hellospring.model.Message;

public class MessageResponse {
    private Long id;
    private String content;
    private String authorUsername;
    private LocalDateTime createdAt;


    public MessageResponse() {}

    public MessageResponse(Long id, String content, String authorUsername, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.authorUsername = authorUsername;
        this.createdAt = createdAt;
    }

    public static MessageResponse fromEntity(Message message) {
        return new MessageResponse(
            message.getId(),
            message.getContent(),
            message.getAuthor().getUsername(), 
            message.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
