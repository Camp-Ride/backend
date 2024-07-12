package com.richjun.campride.chat.service;

import com.richjun.campride.chat.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class KafkaConsumerService {

    private final RedisTemplate<String, Object> redisTemplate;


    @KafkaListener(topics = "chat-topic", groupId = "chat-group")
    public void listen(ChatMessage message) {

        log.info("received : " + message.toString());

        redisTemplate.opsForList().rightPush("chat-messages", message.getContent());
    }
}