package com.richjun.campride.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.chat.domain.ChatMessage;
import com.richjun.campride.chat.repository.ChatMessageRedisRepository;
import com.richjun.campride.chat.response.ChatMessageResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Transactional
@Service
public class ChatService {

    private final ChatMessageRedisRepository chatMessageRedisRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;


    public void sendMessage(ChatMessage message) {
        simpMessagingTemplate.convertAndSend("/topic/messages/room/" + message.getRoomId(), message);
    }

    public RecordId addMessage(String roomId, String messageContent) {
        return chatMessageRedisRepository.addMessage(roomId, messageContent);
    }

    public List<ChatMessageResponse> getMessages(String roomId, int startOffset, int count) {

        List<MapRecord<String, String, Map<String, String>>> records = chatMessageRedisRepository.getMessages(
                roomId, startOffset, count);

        return records.stream()
                .map(record -> {
                    try {
                        String messageJson = String.valueOf(record.getValue().get("message"));
                        ChatMessage chatMessage = objectMapper.readValue(messageJson, ChatMessage.class);

                        return ChatMessageResponse.from(String.valueOf(record.getId()), chatMessage); // 정적 팩토리 메서드 사용
                    } catch (ClassCastException | JsonProcessingException e) {
                        log.error("Error processing message: {}", record.getValue().get("message"), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }
}