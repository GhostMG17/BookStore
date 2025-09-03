package com.example.BookStore.service;

import com.example.BookStore.entity.Message;
import com.example.BookStore.entity.User;
import com.example.BookStore.repository.MessageRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository repo;

    public MessageService(MessageRepository repo) {
        this.repo = repo;
    }

    public Message save(Message message) {
        return repo.save(message);
    }

    public List<Message> getChatHistory(User user, User admin) {
        return repo.getChatHistory(user, admin);
    }
}
