package com.richjun.campride.global.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FCMMessageRequest {
    private boolean validateOnly;
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private Notification notification;
        private String token;
        private Data data;
        private Apns apns;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data {
        private String name;
        private String description;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Apns {
        private Payload payload;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Payload {
        private Aps aps;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Aps {
        private String sound;
    }

}