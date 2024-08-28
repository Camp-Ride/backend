package com.richjun.campride.user.response;

import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long userId;
    private String nickname;


    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getNickname());
    }

}
