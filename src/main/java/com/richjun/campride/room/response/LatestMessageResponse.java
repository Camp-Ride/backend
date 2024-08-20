package com.richjun.campride.room.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LatestMessageResponse {
    private String sender;
    private String content;

    public static LatestMessageResponse of(String sender, String content) {
        return new LatestMessageResponse(sender, content);
    }

}
