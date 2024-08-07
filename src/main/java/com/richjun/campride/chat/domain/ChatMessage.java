package com.richjun.campride.chat.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage implements Serializable {
    private String chatMessageId;
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


    @Override
    public String toString() {
        return "{" +
                "\"chatMessageId\":\"" + chatMessageId + "\"," +
                "\"roomId\":" + roomId + "," +
                "\"userId\":\"" + userId + "\"," +
                "\"text\":\"" + text + "\"," +
                "\"imageUrl\":\"" + imageUrl + "\"," +
                "\"timestamp\":\"" + timestamp + "\"," +
                "\"chatMessageType\":\"" + chatMessageType + "\"," +
                "\"reactions\":" + reactions + "," +
                "\"isReply\":" + isReply + "," +
                "\"replyingMessage\":\"" + replyingMessage + "\"" +
                '}';
    }

    public void updateChatMessageId(String chatMessageId) {
        this.chatMessageId = chatMessageId;
    }
}