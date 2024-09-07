package com.richjun.campride.chat.domain.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.chat.domain.ChatMessage;
import com.richjun.campride.chat.domain.ChatMessageType;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.global.exception.ExceptionCode;
import com.richjun.campride.room.response.LatestMessageResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
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

        return redisTemplate.opsForZSet()
                .reverseRange("/room/" + roomId, startOffset * 10, (startOffset * 10) + count - 1).stream()
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
            log.info(messageJson);
            return objectMapper.readValue(messageJson.replace("\n", "\\n"), ChatMessage.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize ChatMessage", e);
        }
    }

    public LatestMessageResponse getLatestMessage(Long id) {

        String value = redisTemplate.opsForZSet().reverseRange("/room/" + id, 0, 0).stream().findFirst().orElse(null);

        if (value == null) {
            return LatestMessageResponse.of("", "", "", null, LocalDateTime.MIN);
        }
        log.info(value);
        try {
            JsonNode jsonNode = objectMapper.readTree(value.replace("\n", "\\n"));

            String sender = jsonNode.path("userId").asText();
            String content = jsonNode.path("text").asText();
            String nickname = jsonNode.path("userNickname").asText();
            ChatMessageType chatMessageType = ChatMessageType.valueOf(jsonNode.path("chatMessageType").asText());
            LocalDateTime timestamp = objectMapper.convertValue(jsonNode.path("timestamp"), LocalDateTime.class);

            return LatestMessageResponse.of(sender, content, nickname, chatMessageType, timestamp);
        } catch (JsonProcessingException e) {
            throw new BadRequestException(ExceptionCode.FAIL_JSON_PARSING);
        }

    }

    public Long getUnreadMessageCount(Long id, Long lastSeenMessageScore) {

        if (lastSeenMessageScore == null) {
            return redisTemplate.opsForZSet().size("/room/" + id);
        }

        Long count = redisTemplate.opsForZSet()
                .count("/room/" + id, lastSeenMessageScore + 1, Long.MAX_VALUE);

        return count;

    }

    public Long getLatestMessageScore(Long id) {

        String value = redisTemplate.opsForZSet().reverseRange("/room/" + id, 0, 0).stream().findFirst().orElse(null);

        if (value == null) {
            return 0L;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(value.replace("\n", "\\n"));
            Long lastSeenMessageScore = Long.parseLong(jsonNode.path("id").asText());
            return lastSeenMessageScore;
        } catch (JsonProcessingException e) {
            throw new BadRequestException(ExceptionCode.FAIL_JSON_PARSING);
        }

    }

    public void deleteRoomMessages(Long id) {
        redisTemplate.delete("/room/" + id);
    }
}
