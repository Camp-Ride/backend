package com.richjun.campride.global.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FCMMessageRequest {
    private boolean validateOnly;
    private FCMMessageRequest.Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private FCMMessageRequest.Notification notification;
        private String token;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }
}