package com.richjun.campride.chat.service;

import com.richjun.campride.chat.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class KafkaConsumerService {


    private final ChatService chatService;

    @KafkaListener(topics = "chat-topic", groupId = "chat-group")
    public void listen(ChatMessage message) {
        log.info("received : " + message.toString());

        RecordId recordId = chatService.addMessage(message.getRoomId().toString(), message.toString());
        message.updateChatMessageId(String.valueOf(recordId));
        chatService.sendMessage(message);

    }


}