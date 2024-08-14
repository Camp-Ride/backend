package com.richjun.campride.chat.domain.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.chat.domain.ChatMessage;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@AllArgsConstructor
public class ChatMessageRedisTemplateRepository {


    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;


    public void addMessage(ChatMessage chatMessage) {

        Long messageId = redisTemplate.opsForValue().increment("chat:message:id");
        chatMessage.updateChatMessageId(messageId);

        redisTemplate.opsForZSet().add("/room/" + chatMessage.getRoomId(), chatMessage.toString(), messageId);
    }


    public List<ChatMessage> getMessages(Long roomId, int startOffset,
                                         int count) {

        log.info(redisTemplate.opsForZSet().reverseRange("/room/" + roomId, startOffset * 10, count - 1).toString());

        return redisTemplate.opsForZSet().reverseRange("/room/" + roomId, startOffset * 10, count - 1).stream()
                .map(this::deserializeChatMessage)
                .collect(Collectors.toList());
    }


    public void deleteMessage(ChatMessage message) {
        redisTemplate.opsForZSet().removeRangeByScore("/room/" + message.getRoomId(), message.getId(), message.getId());

    }

    public void updateReaction(ChatMessage message) {
        redisTemplate.opsForZSet().add("/room/" + message.getRoomId(), message.toString(), message.getId());
    }


    private ChatMessage deserializeChatMessage(String messageJson) {
        try {
            return objectMapper.readValue(messageJson, ChatMessage.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize ChatMessage", e);
        }
    }
}
