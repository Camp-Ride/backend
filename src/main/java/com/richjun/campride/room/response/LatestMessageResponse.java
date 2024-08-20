package com.richjun.campride.room.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LatestMessageResponse {
    private String sender;
    private String content;
    private LocalDateTime createdAt;


    public static LatestMessageResponse of(String sender, String content, LocalDateTime createdAt) {
        return new LatestMessageResponse(sender, content, createdAt);
    }

}
