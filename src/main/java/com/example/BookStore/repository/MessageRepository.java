package com.example.BookStore.repository;

import com.example.BookStore.entity.Message;
import com.example.BookStore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m " +
            "WHERE (m.sender = :user AND m.receiver = :admin) " +
            "   OR (m.sender = :admin AND m.receiver = :user) " +
            "ORDER BY m.timestamp ASC")
    List<Message> getChatHistory(User user, User admin);
}
