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
    private Long id;
    private Long roomId;
    private String userId;
    private String userNickname;
    private String text;
    private String imageUrl;
    private LocalDateTime timestamp;
    private ChatMessageType chatMessageType;
    private List<ChatReaction> reactions;
    private Boolean isReply;
    private String replyingMessage;

    public static ChatMessageResponse from(Long id, ChatMessage chatMessage) {
        return new ChatMessageResponse(
                id,
                chatMessage.getRoomId(),
                chatMessage
                        .getUserId(),
                chatMessage.getUserNickname(),
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