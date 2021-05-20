package com.tango.controllers;

import com.tango.DTO.MessageDTO;
import com.tango.models.chat.ChatMessage;
import com.tango.models.chat.message.Message;
import com.tango.models.chat.room.ChatRoom;
import com.tango.models.chat.user.ChatUser;
import com.tango.services.ChatUserService;
import com.tango.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ChatController {

    //    @MessageMapping("/chat.sendMessage")
//    @SendTo("/topic/public")
//    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
//        return chatMessage;
//    }
    final ChatUserService chatUserService;
    final MessageService messageService;

    public ChatController(@Autowired ChatUserService chatUserService,
                          @Autowired MessageService messageService) {
        this.chatUserService = chatUserService;
        this.messageService = messageService;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ResponseEntity<?> sendMessage(@Payload MessageDTO messageDTO) {
        System.out.println(messageDTO);
        try {
            Message message = messageService.toMessageWithSave(messageDTO);
            ChatUser chatUser = message.getChatUser();

            Map<String, Object> response = new HashMap<>();
            response.put("username", chatUser.getUser().getUsername());
            response.put("avatar", chatUser.getUser().getAvatar());
            response.put("posted", message.getPosted());
            response.put("message", message.getMessage());
            response.put("messageType", message.getMessageType());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println(e.getMessage() + "\n\n\n");
            throw e;
        }
    }
}