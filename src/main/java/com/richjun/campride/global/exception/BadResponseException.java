package com.richjun.campride.global.exception;

import lombok.Getter;

@Getter
public class BadResponseException extends RuntimeException {
    private final int code;
    private final String message;

    public BadResponseException(final ExceptionCode exceptionCode) {
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
    }
}