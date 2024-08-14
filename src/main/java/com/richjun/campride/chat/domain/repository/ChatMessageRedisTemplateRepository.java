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


    public RecordId addMessage(String roomId, String messageContent) {

        return redisTemplate.opsForStream().add("/room/" + roomId, Map.of("message", messageContent));
    }

    public void addMessage2(ChatMessage chatMessage) {

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


    private String getStreamIdAtOffset(String roomId, int offset) {
        StreamOperations<String, String, Map<String, String>> streamOps = redisTemplate.opsForStream();
        List<MapRecord<String, String, Map<String, String>>> records = streamOps.reverseRange(
                "/room/" + roomId, Range.open("-", "+"), Limit.limit().count(offset + 1));

        if (records != null && records.size() > 0) {
            log.info("records: {}", records);
            return records.get(records.size() - 1).getId().getValue();  // Ensuring we don't go out of range
        } else {
            log.info("$");
            return "$";  // If the offset is out of range, use the latest ID
        }
    }



    private ChatMessage deserializeChatMessage(String messageJson) {
        try {
            return objectMapper.readValue(messageJson, ChatMessage.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize ChatMessage", e);
        }
    }
}
