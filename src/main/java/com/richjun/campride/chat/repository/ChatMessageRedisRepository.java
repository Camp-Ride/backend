package com.richjun.campride.chat.repository;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ChatMessageRedisRepository {


    private final StringRedisTemplate redisTemplate;


    public RecordId addMessage(String roomId, String messageContent) {
        return redisTemplate.opsForStream().add("room" + roomId, Map.of("message", messageContent));

    }


    public List<MapRecord<String, String, Map<String, String>>> getMessages(String roomId, String startOffset,
                                                                            int count) {
        StreamOperations<String, String, Map<String, String>> streamOps = redisTemplate.opsForStream();

        return streamOps.read(
                StreamReadOptions.empty().count(count),
                StreamOffset.create("room" + roomId, ReadOffset.from(startOffset))
        );

    }


}