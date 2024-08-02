package com.richjun.campride.chat.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
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

    // Getters and Setters

    @Override
    public String toString() {
        return "ChatMessage{" +
                "roomId=" + roomId +
                ", userId='" + userId + '\'' +
                ", text='" + text + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", timestamp=" + timestamp +
                ", isSender=" + isSender +
                ", chatMessageType=" + chatMessageType +
                ", reactions=" + reactions +
                ", isReply=" + isReply +
                ", replyingMessage='" + replyingMessage + '\'' +
                '}';
    }
}