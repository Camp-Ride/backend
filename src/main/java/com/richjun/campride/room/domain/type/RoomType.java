package com.richjun.campride.room.domain.type;

public enum RoomType {
    ONE, ROUND;

    public static RoomType fromString(String value) {
        for (RoomType type : RoomType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당하는 RoomType이 존재하지 않습니다.: " + value);
    }
}
