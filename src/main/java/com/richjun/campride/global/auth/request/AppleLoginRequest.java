package com.richjun.campride.global.auth.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppleLoginRequest {
    private String identityToken;
    private String deviceToken;
}
