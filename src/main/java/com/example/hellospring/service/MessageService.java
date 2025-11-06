package com.example.hellospring.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hellospring.exception.MessageNotFoundException;
import com.example.hellospring.model.Message;
import com.example.hellospring.model.User;
import com.example.hellospring.repository.MessageRepository;
import com.example.hellospring.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        initializeSampleData();
    }

    @Transactional
    private void initializeSampleData() {
        // Создание пользователей
        User user1 = userRepository.save(new User("Alice"));
        User user2 = userRepository.save(new User("Bob"));

        // Создание сообщений
        messageRepository.save(new Message("Hello, JPA and Hibernate!", user1));
        messageRepository.save(new Message("This uses Spring Data JPA interface.", user2));
        messageRepository.save(new Message("Relationships are now managed by ORM.", user1));
    }

    public Message findMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException("Сообщение с ID " + id + " не найдено"));
    }

    public List<Message> getMessages(int page, int size) {
        // Используем стандартный метод Spring Data JPA с пагинацией
        return messageRepository.findAll(
            org.springframework.data.domain.PageRequest.of(page, size)
        ).getContent();
    }

    @Transactional
    public Message saveMessage(String content, String username) {
        // 1. Найти существующего пользователя или создать нового
        User author = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(new User(username)));

        // 2. Создать и сохранить сообщение
        Message message = new Message(content, author);
        return messageRepository.save(message);
    }

    public Message saveMessage(Message message) {
        // Если автор не указан или не имеет username, используем Anonymous
        if (message.getAuthor() == null || message.getAuthor().getUsername() == null || 
            message.getAuthor().getUsername().trim().isEmpty()) {
            User anonymous = userRepository.findByUsername("Anonymous")
                    .orElseGet(() -> userRepository.save(new User("Anonymous")));
            message.setAuthor(anonymous);
        } else {
            // Если указан username, найдем или создадим пользователя
            String username = message.getAuthor().getUsername();
            User user = userRepository.findByUsername(username)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setUsername(username);
                        return userRepository.save(newUser);
                    });
            message.setAuthor(user);
        }
        return messageRepository.save(message);
    }

    public void saveAll(List<Message> messages) {
        messageRepository.saveAll(messages);
    }

    public List<Message> findMessagesByAuthor(String username) {
        // Используем стандартный метод Spring Data JPA
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + username));
        return user.getMessages();
    }

    // Демонстрация проблемы N+1. Показывает, что будет выполнено N+1 запросов в базе данных.
    @Transactional
    public List<Message> getLatestMessagesNPlus1() {
        System.out.println("--- ЗАПРОС N+1 ---");
        List<Message> messages = messageRepository.findTop30ByOrderByCreatedAtDesc();
        
        // Принудительная загрузка автора для избежания Proxy Exception в контроллере. Демонстрирует N+1
        messages.forEach(m -> m.getAuthor().getUsername());
        
        return messages;
    }

    /**
     * Вариант B: Решение проблемы N+1 с помощью JOIN FETCH.
     * Все данные загружаются ОДНИМ запросом.
     */
    @Transactional
    public List<Message> getLatestMessagesOptimized() {
        System.out.println("--- ОПТИМИЗИРОВАННЫЙ ЗАПРОС JOIN FETCH ---");
        return messageRepository.findLatestWithAuthors();
    }
    
    //Оригинальный метод - демонстрация проблемы LazyInitializationException
    public Message getMessageToFail(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException("Сообщение с ID " + id + " не найдено"));
    }

    // Решение 1 проблемы LazyInitializationException: (Способ JPA): Решение с помощью @Transactional
    @Transactional
    public Message getMessageWithTransactional(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException("Сообщение с ID " + id + " не найдено"));
        // Принудительная инициализация lazy-поля внутри транзакции
        message.getAuthor().getUsername();
        return message;
    }

    /**
     * Решение 2 проблемы LazyInitializationException с помощью DTO
     * Метод БЕЗ @Transactional - возвращает Entity.
     * Преобразование Entity в DTO происходит в контроллере внутри транзакции.
     */
    public Message getMessageWithDTO(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException("Сообщение с ID " + id + " не найдено"));
    }
}
