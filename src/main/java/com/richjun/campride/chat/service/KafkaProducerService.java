package com.richjun.campride.chat.service;

import com.richjun.campride.chat.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    public void sendMessage(ChatMessage chatMessage) {
        kafkaTemplate.send("chat-topic", chatMessage);
        log.info("sent : " + chatMessage.getText());
    }

    public void sendReaction(ChatMessage chatMessage) {
        kafkaTemplate.send("reaction-topic", chatMessage);
    }

    public void sendLeave(ChatMessage chatMessage) {
        kafkaTemplate.send("leave-topic", chatMessage);
    }

    public void sendKick(ChatMessage chatMessage) {
        kafkaTemplate.send("kick-topic", chatMessage);
    }

    public void sendJoin(ChatMessage chatMessage) {
        kafkaTemplate.send("join-topic", chatMessage);
    }

}
