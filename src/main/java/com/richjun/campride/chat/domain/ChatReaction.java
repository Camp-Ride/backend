package com.richjun.campride.chat.domain;

public class ChatReaction {

    private String userId;
    private ChatReactionType chatReactionType;

    // Getters and Setters

    @Override
    public String toString() {
        return "Reaction{" +
                "userId='" + userId + '\'' +
                ", reactionType='" + chatReactionType +
                '}';
    }
}