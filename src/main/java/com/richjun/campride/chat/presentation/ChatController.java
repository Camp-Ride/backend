package com.richjun.campride.chat.presentation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.richjun.campride.chat.domain.ChatMessage;
import com.richjun.campride.chat.domain.ChatMessageType;
import com.richjun.campride.chat.domain.ChatReaction;
import com.richjun.campride.chat.response.ChatMessageResponse;
import com.richjun.campride.chat.service.ChatService;
import com.richjun.campride.chat.service.KafkaProducerService;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final KafkaProducerService kafkaProducerService;
    private final ChatService chatService;


    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        log.info(chatMessage.toString());
        kafkaProducerService.sendMessage(chatMessage);
    }

    @MessageMapping("/send/reaction")
    public void sendReaction(@Payload ChatMessage chatMessage) {
        log.info(chatMessage.toString());
        kafkaProducerService.sendReaction(chatMessage);
    }

    @GetMapping("/messages")
    public List<ChatMessageResponse> getMessages(@RequestParam Long roomId, @RequestParam int startOffset,
                                                 @RequestParam int count) {
        return chatService.getMessages(roomId, startOffset, count);
    }

    @GetMapping("/messages/latest")
    public List<ChatMessageResponse> getLatest10Messages(@RequestParam Long roomId) {
        return chatService.getLatest10Messages(roomId);
    }


    @PostMapping("/messages/stomp/test")
    public void testStomp() {
        log.info("test stomp");
        kafkaProducerService.sendMessage(
                new ChatMessage(1L, 1L, "test", "test", "test", LocalDateTime.now(), List.of(), "test",
                        ChatMessageType.TEXT, false));
    }


}