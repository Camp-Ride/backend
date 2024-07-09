package com.richjun.campride.global.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuth2UserResponse {

    private String username;
    private String nickname;
    private String role;

}