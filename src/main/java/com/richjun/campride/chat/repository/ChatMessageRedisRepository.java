package com.richjun.campride.chat.repository;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Jedis;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@AllArgsConstructor
public class ChatMessageRedisRepository {


    private final StringRedisTemplate redisTemplate;


    public RecordId addMessage(String roomId, String messageContent) {

        return redisTemplate.opsForStream().add("/room/" + roomId, Map.of("message", messageContent));
    }


    public List<MapRecord<String, String, Map<String, String>>> getMessages(String roomId, int startOffset,
                                                                            int count) {
        StreamOperations<String, String, Map<String, String>> streamOps = redisTemplate.opsForStream();

        String startId = getStreamIdAtOffset(roomId, startOffset * 10);

        log.info("startId: {}", startId);
        log.info("endId: {}", "-");
        log.info(String.valueOf(startOffset * 10));

        return streamOps.range("/room/" + roomId, Range.closed(startId, "+"), Limit.limit().count(count));
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


}
