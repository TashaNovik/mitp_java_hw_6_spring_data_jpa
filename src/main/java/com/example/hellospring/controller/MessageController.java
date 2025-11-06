package com.example.hellospring.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.hellospring.dto.CreateMessageRequest;
import com.example.hellospring.dto.MessageResponse;
import com.example.hellospring.model.Message;
import com.example.hellospring.service.MessageService;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) { this.service = service; }

    @GetMapping
    public List<Message> getMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.getMessages(page, size);
    }

    @GetMapping("/{id}")
    public Message getMessage(@PathVariable Long id) {
        return service.findMessageById(id);
    }

    @GetMapping("/search")
    public List<Message> searchMessagesByAuthor(@RequestParam String author) {
        return service.findMessagesByAuthor(author);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Message postMessage(@RequestBody CreateMessageRequest request) {
        return service.saveMessage(request.getContent(), request.getUsername());
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public void postMessages(@RequestBody List<Message> messages) {
        service.saveAll(messages);
    }

    @GetMapping("/nplus1")
    public List<Message> getMessagesNPlus1() {
        return service.getLatestMessagesNPlus1();
    }

    @GetMapping("/optimized")
    public List<Message> getMessagesOptimized() {
        return service.getLatestMessagesOptimized();
    }

    // Демонстрация проблемы LazyInitializationException
    @GetMapping("/fail/{id}")
    public ResponseEntity<?> getMessageToFail(@PathVariable Long id) {
        try {
            Message message = service.getMessageToFail(id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            String errorMsg = "ОШИБКА! Проверьте консоль. Вероятно, LazyInitializationException (Proxy Exception)" +
                    " потому что вы пытаетесь получить LAZY-поле вне транзакции. " +
                    "Решения:\n" +
                    "1. Способ JPA: /api/messages/transactional/" + id + "\n" +
                    "2. Способ DTO : /api/messages/dto/" + id + "\n\n" +
                    "Ошибка: " + e.getClass().getSimpleName();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg);
        }
    }

    // Решение 1 проблемы LazyInitializationException: (Способ JPA): Решение с помощью @Transactional
    @GetMapping("/transactional/{id}")
    public ResponseEntity<Message> getMessageWithTransactional(@PathVariable Long id) {
        Message message = service.getMessageWithTransactional(id);
        return ResponseEntity.ok(message);
    }

    // Решение 2 проблемы LazyInitializationException с помощью DTO
    @GetMapping("/dto/{id}")
    @Transactional
    public ResponseEntity<MessageResponse> getMessageWithDTO(@PathVariable Long id) {
        Message message = service.getMessageWithDTO(id);
        MessageResponse response = MessageResponse.fromEntity(message);
        return ResponseEntity.ok(response);
    }
}
