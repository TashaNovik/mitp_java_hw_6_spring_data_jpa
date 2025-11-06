package com.example.hellospring.service;

import com.example.hellospring.exception.MessageNotFoundException;
import com.example.hellospring.model.Message;
import com.example.hellospring.model.User;
import com.example.hellospring.repository.JdbcMessageRepository;
import com.example.hellospring.repository.JdbcUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private final JdbcMessageRepository messageRepository;
    private final JdbcUserRepository userRepository;

    public MessageService(JdbcMessageRepository messageRepository, JdbcUserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public Message findMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException("Сообщение с ID " + id + " не найдено"));
    }

    public List<Message> getMessages(int page, int size) {
        return messageRepository.findAllPaginated(page, size);
    }

    public Message saveMessage(Message message) {
        // Если автор анонимный, найдем или создадим пользователя "Anonymous"
        if (message.getAuthor() == null || message.getAuthor().getUsername() == null) {
            User anonymous = userRepository.findByUsername("Anonymous")
                    .orElseGet(() -> userRepository.save(new User("Anonymous")));
            message.setAuthor(anonymous);
        }
        return messageRepository.save(message);
    }

    public void saveAll(List<Message> messages) {
        messageRepository.saveAll(messages);
    }

    public List<Message> findMessagesByAuthor(String username) {
        return messageRepository.findByAuthorUsername(username);
    }
}
