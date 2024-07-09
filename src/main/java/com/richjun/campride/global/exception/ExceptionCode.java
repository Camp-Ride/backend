package com.richjun.campride.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    INVALID_REQUEST(1000, "올바르지 않은 요청입니다."),

    FAIL_TO_GENERATE_RANDOM_NICKNAME(1001, "랜덤한 닉네임을 생성하는데 실패하였습니다."),


    FAIL_TO_GET_LOCATION(2000, "Location을 불러오는데 실패하였습니다."),

    NOT_FOUND_USER_ID(3000, "해당하는 User Id를 찾지 못했습니다."),
    NOT_FOUND_ROOM_ID(3001, "해당하는 Room Id를 찾지 못했습니다.");



    private final int code;
    private final String message;
}