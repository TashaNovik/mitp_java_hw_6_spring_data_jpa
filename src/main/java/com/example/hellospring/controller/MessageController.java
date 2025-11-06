package com.example.hellospring.controller;

import com.example.hellospring.model.Message;
import com.example.hellospring.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) { this.service = service; }

    // Пагинация
    @GetMapping
    public List<Message> getMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.getMessages(page, size);
    }

    // Обработка ошибок
    @GetMapping("/{id}")
    public Message getMessage(@PathVariable Long id) {
        return service.findMessageById(id);
    }

    // Поиск по автору
    @GetMapping("/search")
    public List<Message> searchMessagesByAuthor(@RequestParam String author) {
        return service.findMessagesByAuthor(author);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Message postMessage(@RequestBody Message message) {
        return service.saveMessage(message);
    }

    // Батч-операция
    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public void postMessages(@RequestBody List<Message> messages) {
        service.saveAll(messages);
    }
}
