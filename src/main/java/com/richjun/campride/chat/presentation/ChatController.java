package com.richjun.campride.chat.presentation;

import com.richjun.campride.chat.domain.ChatMessage;
import com.richjun.campride.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/sendMessage")
//    @SendTo("/topic/messages")
    public ChatMessage sendMessage(@RequestBody ChatMessage chatMessage) {
        log.info("chatMessage: " + chatMessage.getRoomId() + " " + chatMessage.getSender() + " "
                + chatMessage.getContent() + " " + chatMessage.getRecipientToken());
        // 메시지를 Kafka에 게시
        chatService.sendMessage(chatMessage);
        // 실시간으로 클라이언트에 브로드캐스트
        return chatMessage;
    }
}