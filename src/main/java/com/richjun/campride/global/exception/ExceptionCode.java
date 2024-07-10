package com.richjun.campride.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    INVALID_REQUEST(1000, "올바르지 않은 요청입니다."),

    FAIL_TO_GENERATE_RANDOM_NICKNAME(1001, "랜덤한 닉네임을 생성하는데 실패하였습니다."),


    FAIL_TO_GET_LOCATION(2001, "Location을 불러오는데 실패하였습니다."),

    NOT_FOUND_USER_ID(3001, "해당하는 User Id를 찾지 못했습니다."),
    NOT_FOUND_ROOM_ID(3002, "해당하는 Room Id를 찾지 못했습니다."),

    NOT_ROOM_LEADER(4001, "해당하는 Room의 방장이 아니기 때문에 요청을 실행할 수 없습니다."),


    EXCEED_IMAGE_CAPACITY(5001, "업로드 가능한 이미지 용량을 초과했습니다."),

    NULL_IMAGE(5002, "업로드한 이미지 파일이 NULL입니다."),
    EMPTY_IMAGE_LIST(5003, "최소 한 장 이상의 이미지를 업로드해야합니다."),

    EXCEED_IMAGE_LIST_SIZE(5004, "업로드 가능한 이미지 개수를 초과했습니다."),
    INVALID_IMAGE_URL(5005, "요청한 이미지 URL의 형식이 잘못되었습니다."),
    INVALID_IMAGE_PATH(5101, "이미지를 저장할 경로가 올바르지 않습니다."),
    FAIL_IMAGE_NAME_HASH(5102, "이미지 이름을 해싱하는 데 실패했습니다."),
    INVALID_IMAGE(5103, "올바르지 않은 이미지 파일입니다.");


    private final int code;
    private final String message;
}