package com.richjun.campride.chat.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatReaction {

    private String userId;

    @JsonProperty("reactionType")
    private ChatReactionType chatReactionType;

    // Getters and Setters

    @Override
    public String toString() {
        return "{" +
                "userId='" + userId + '\'' +
                ", reactionType=\"" + chatReactionType +
                "\"}";
    }
}