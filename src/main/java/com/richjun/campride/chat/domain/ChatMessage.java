package com.richjun.campride.chat.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("chatMessage")
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage implements Serializable {

    @Id
    private Long id;

    @Indexed
    private Long roomId;
    private String userId;
    private String userNickname;
    private String text;
    private String imageUrl;
    private LocalDateTime timestamp;
    private List<ChatReaction> reactions;
    private String replyingMessage;

    @JsonProperty("chatMessageType")
    private ChatMessageType chatMessageType;
    @JsonProperty("isReply")
    private boolean isReply;


    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\"," +
                "\"roomId\":" + roomId + "," +
                "\"userId\":\"" + userId + "\"," +
                "\"userNickname\":\"" + userNickname + "\"," +
                "\"text\":\"" + text + "\"," +
                "\"imageUrl\":\"" + imageUrl + "\"," +
                "\"timestamp\":\"" + timestamp + "\"," +
                "\"chatMessageType\":\"" + chatMessageType + "\"," +
                "\"reactions\":" + reactions + "," +
                "\"isReply\":" + isReply + "," +
                "\"replyingMessage\":\"" + replyingMessage + "\"" +
                '}';
    }

    public void updateChatMessageId(Long chatMessageId) {
        this.id = chatMessageId;
    }
}