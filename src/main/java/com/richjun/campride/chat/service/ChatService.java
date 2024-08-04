package com.richjun.campride.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.chat.domain.ChatMessage;
import com.richjun.campride.chat.domain.ChatMessageType;
import com.richjun.campride.chat.repository.ChatMessageRedisRepository;
import com.richjun.campride.chat.response.ChatMessageResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ChatService {

    private final StringRedisTemplate redisTemplate;
    private final ChatMessageRedisRepository chatMessageRedisRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;


    public void sendMessage(ChatMessage message) {
        simpMessagingTemplate.convertAndSend("/topic/messages/" + message.getRoomId(), message);
    }

    public void addMessage(String roomId, String messageContent) {
        chatMessageRedisRepository.addMessage(roomId, messageContent);
    }

    public List<ChatMessageResponse> getMessages(String roomId, String startOffset, int count) {

        List<MapRecord<String, String, Map<String, String>>> records = chatMessageRedisRepository.getMessages(
                roomId, startOffset, count);

        return records.stream()
                .map(record -> {
                    try {
                        String messageJson = String.valueOf(record.getValue().get("message"));
                        log.info(messageJson.toString());

                        ChatMessage chatMessage = objectMapper.readValue(messageJson, ChatMessage.class);
                        return ChatMessageResponse.from(chatMessage); // 정적 팩토리 메서드 사용
                    } catch (ClassCastException | JsonProcessingException e) {
                        log.error("Error processing message: {}", record.getValue().get("message"), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }
}