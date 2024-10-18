package com.richjun.campride.global.auth.request;

import lombok.Getter;

@Getter
public class AppleLoginRequest {
    private String identityToken;
    private String deviceToken;

    public AppleLoginRequest(String identityToken, String deviceToken) {
        this.identityToken = identityToken;
        this.deviceToken = deviceToken;
    }

}
