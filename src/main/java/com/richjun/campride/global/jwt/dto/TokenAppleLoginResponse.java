package com.richjun.campride.global.jwt.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class TokenAppleLoginResponse {

    String accessToken;
    String refreshToken;
    Boolean isNicknameUpdated;

}
