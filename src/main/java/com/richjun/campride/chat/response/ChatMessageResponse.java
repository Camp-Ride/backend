package com.richjun.campride.chat.response;

import com.richjun.campride.chat.domain.ChatMessage;
import com.richjun.campride.chat.domain.ChatMessageType;
import com.richjun.campride.chat.domain.ChatReaction;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageResponse {
    private String chatMessageId;
    private Long roomId;
    private String userId;
    private String text;
    private String imageUrl;
    private LocalDateTime timestamp;
    private ChatMessageType chatMessageType;
    private List<ChatReaction> reactions;
    private Boolean isReply;
    private String replyingMessage;

    public static ChatMessageResponse from(String chatMessageId, ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessageId,
                chatMessage.getRoomId(),
                chatMessage
                        .getUserId(),
                chatMessage.getText(),
                chatMessage.getImageUrl(),
                chatMessage.getTimestamp(),
                chatMessage.getChatMessageType(),
                chatMessage.getReactions(),
                chatMessage.isReply(),
                chatMessage.getReplyingMessage()
        );
    }


}