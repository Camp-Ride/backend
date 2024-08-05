package com.richjun.campride.chat.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage implements Serializable {
    private Long roomId;
    private String userId;
    private String text;
    private String imageUrl;
    private LocalDateTime timestamp;
    private List<ChatReaction> reactions;
    private String replyingMessage;

    @JsonProperty("chatMessageType")
    private ChatMessageType chatMessageType;
    @JsonProperty("isReply")
    private boolean isReply;
    @JsonProperty("isSender")
    private boolean isSender;



    @Override
    public String toString() {
        return "{" +
                "\"roomId\":" + roomId + "," +
                "\"userId\":\"" + userId + "\"," +
                "\"text\":\"" + text + "\"," +
                "\"imageUrl\":\"" + imageUrl + "\"," +
                "\"timestamp\":\"" + timestamp + "\"," +
                "\"isSender\":" + isSender + "," +
                "\"chatMessageType\":\"" + chatMessageType + "\"," +
                "\"reactions\":" + reactions + "," +
                "\"isReply\":" + isReply + "," +
                "\"replyingMessage\":\"" + replyingMessage + "\"" +
                '}';
    }
}