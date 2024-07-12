package com.richjun.campride.chat.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    private String roomId;
    private String sender;
    private String content;
    private String recipientToken;

    @Override
    public String toString() {
        return "ChatMessage [roomId=" + roomId + ", sender=" + sender + ", content=" + content + ", recipientToken="
                + recipientToken + "]";
    }
}