package com.example.BookStore.controller;

import com.example.BookStore.dto.ChatMessageDTO;
import com.example.BookStore.entity.Message;
import com.example.BookStore.entity.User;
import com.example.BookStore.service.MessageService;
import com.example.BookStore.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserService userService;

    public WebSocketController(SimpMessagingTemplate messagingTemplate,
                               MessageService messageService,
                               UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.userService = userService;
    }

    @MessageMapping("/private-message")
    public void sendPrivateMessage(ChatMessageDTO dto) {
        User sender = userService.findById(dto.getSenderId());
        User admin = userService.findById(1L); // всегда админ

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(admin);
        message.setContent(dto.getContent());
        messageService.save(message);

        // Если сообщение от пользователя админу
        if (!sender.getId().equals(admin.getId())) {
            // Отправляем только админу (чтобы он видел)
            messagingTemplate.convertAndSend("/topic/admin", new ChatMessageDTO(message));
        } else {
            // Если админ отвечает пользователю
            Long receiverId = dto.getReceiverId(); // id пользователя, которому админ отвечает
            User receiver = userService.findById(receiverId);

            Message adminMessage = new Message();
            adminMessage.setSender(admin);
            adminMessage.setReceiver(receiver);
            adminMessage.setContent(dto.getContent());
            messageService.save(adminMessage);

            // Отправляем только пользователю, чтобы он видел сообщение
            messagingTemplate.convertAndSend("/topic/private-" + receiver.getId(), new ChatMessageDTO(adminMessage));
        }
    }


    @GetMapping("/chat-page")
    public String chatPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if(userDetails != null) {
            // Ищем пользователя по email, возвращаем User из Optional
            User currentUser = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
            model.addAttribute("currentUser", currentUser);
        }
        return "chat/chat-page";
    }

    @GetMapping("/admin-chat")
    public String adminChatPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            // На случай, если нужно вывести данные админа на страницу
            User admin = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Admin not found: " + userDetails.getUsername()));
            model.addAttribute("currentAdmin", admin);
        }
        return "chat/admin-chat"; // путь к thymeleaf файлу admin-chat.html
    }


    @GetMapping("/chat/history/{userId}")
    @ResponseBody
    public List<ChatMessageDTO> getChatHistory(@PathVariable Long userId, @AuthenticationPrincipal UserDetails userDetails) {


        User admin = userService.findById(1L);
        User user = userService.findById(userId);



        return messageService.getConversation(admin, user)
                .stream()
                .map(ChatMessageDTO::new)
                .toList();
    }

    @GetMapping("/chat/users")
    @ResponseBody
    public List<User> getChatUsers() {
        User admin = userService.findById(1L); // твой админ
        return messageService.getUsersWhoChattedWithAdmin(admin);
    }

    @PostMapping("/chat/read/{userId}")
    @ResponseBody
    public void markAsRead(@PathVariable Long userId) {
        User admin = userService.findById(1L);
        User user = userService.findById(userId);
        messageService.markAsRead(user, admin);
    }


}
