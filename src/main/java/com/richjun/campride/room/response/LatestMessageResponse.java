package com.richjun.campride.room.response;

import com.richjun.campride.chat.domain.ChatMessageType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LatestMessageResponse {
    private String sender;
    private String content;
    private String nickname;
    private ChatMessageType chatMessageType;
    private LocalDateTime createdAt;


    public static LatestMessageResponse of(String sender, String nickname, String content,
                                           ChatMessageType chatMessageType,
                                           LocalDateTime createdAt) {
        return new LatestMessageResponse(sender, nickname, content, chatMessageType, createdAt);
    }

}
