package com.example.hellospring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.hellospring.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.author ORDER BY m.createdAt DESC LIMIT 30")
    List<Message> findLatestWithAuthors();
    
    List<Message> findTop30ByOrderByCreatedAtDesc();
}

