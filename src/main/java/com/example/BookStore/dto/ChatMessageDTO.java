package com.example.BookStore.dto;

import com.example.BookStore.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private Long senderId;
    private long receiverId;
    private String content;
    private String timestamp;
    private String senderUsername;   // добавили
    private String receiverUsername; // добавили
    private boolean readStatus;

    public ChatMessageDTO(Message msg) {
        this.senderId = msg.getSender().getId();
        this.receiverId = msg.getReceiver().getId();
        this.senderUsername = msg.getSender().getUsername();
        this.receiverUsername = msg.getReceiver().getUsername();
        this.content = msg.getContent();
        this.timestamp = msg.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.readStatus = msg.isReadStatus();
    }
}
