package com.richjun.campride.global.fcm.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FCMSendRequest {
    private Long userId;

    private String title;

    private String body;

    @Builder(toBuilder = true)
    public FCMSendRequest(Long userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
    }
}
