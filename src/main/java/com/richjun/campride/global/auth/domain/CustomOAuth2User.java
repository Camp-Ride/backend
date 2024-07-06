package com.richjun.campride.global.auth.domain;

import com.richjun.campride.global.auth.response.OAuth2UserResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2UserResponse oAuth2User;

    public CustomOAuth2User(OAuth2UserResponse oAuth2UserResponse) {

        this.oAuth2User = oAuth2UserResponse;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return oAuth2User.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return oAuth2User.getName();
    }

    public String getUsername() {

        return oAuth2User.getUsername();
    }
}