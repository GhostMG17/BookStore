package com.example.BookStore.service;

import com.example.BookStore.entity.user.Message;
import com.example.BookStore.entity.user.User;
import com.example.BookStore.repository.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MessageService {
    private final MessageRepository repo;

    public MessageService(MessageRepository repo) {
        this.repo = repo;
    }

    public Message save(Message message) {
        return repo.save(message);
    }

    public List<Message> getConversation(User admin, User user) {
        return repo.findBySenderAndReceiver(admin, user);
    }

    public List<User> getUsersWhoChattedWithAdmin(User admin) {
        Set<User> users = new HashSet<>();
        users.addAll(repo.findSendersByAdmin(admin.getId()));
        users.addAll(repo.findReceiversByAdmin(admin.getId()));
        users.remove(admin); // убираем самого админа из списка
        return new ArrayList<>(users);
    }

    @Transactional
    public void markAsRead(User sender, User receiver) {
        repo.markMessagesAsRead(sender, receiver);
    }
}
