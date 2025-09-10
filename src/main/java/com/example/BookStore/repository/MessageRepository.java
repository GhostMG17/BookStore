package com.example.BookStore.repository;

import com.example.BookStore.entity.user.Message;
import com.example.BookStore.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m " +
            "WHERE (m.sender = :user AND m.receiver = :admin) " +
            "   OR (m.sender = :admin AND m.receiver = :user) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findBySenderAndReceiver(@Param("user") User user, @Param("admin") User admin);

    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.receiver.id = :adminId")
    List<User> findSendersByAdmin(@Param("adminId") Long adminId);

    @Query("SELECT DISTINCT m.receiver FROM Message m WHERE m.sender.id = :adminId")
    List<User> findReceiversByAdmin(@Param("adminId") Long adminId);


    @Modifying
    @Query("UPDATE Message m SET m.readStatus = true " +
            "WHERE m.sender = :sender AND m.receiver = :receiver AND m.readStatus = false")
    void markMessagesAsRead(@Param("sender") User sender,
                            @Param("receiver") User receiver);


}
