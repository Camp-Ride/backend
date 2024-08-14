package com.richjun.campride.chat.service;

import com.richjun.campride.chat.domain.ChatMessage;
import java.util.ArrayList;
import java.util.List;
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
    public void listenChatTopic(ChatMessage message) {
        log.info("received : " + message.toString());
        chatService.addMessage(message);
        chatService.sendMessage(message);

    }


    @KafkaListener(topics = "reaction-topic", groupId = "chat-group")
    public void listenReactionTopic(ChatMessage message) {
        log.info("received : " + message.toString());
        chatService.addReaction(message);
        chatService.sendReaction(message);

    }


}