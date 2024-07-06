package com.richjun.campride.global.auth.response;

import lombok.Getter;

@Getter
public class OAuth2UserResponse {


    private String username;
    private String name;
    private String role;


    public OAuth2UserResponse(String username, String name, String role) {
        this.username = username;
        this.name = name;
        this.role = role;
    }

    public OAuth2UserResponse(String username, String role) {
        this.username = username;
        this.role = role;
    }
}