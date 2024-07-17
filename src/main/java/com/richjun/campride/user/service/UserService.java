package com.richjun.campride.user.service;

import static com.richjun.campride.global.exception.ExceptionCode.*;

import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.global.exception.ExceptionCode;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import com.richjun.campride.user.request.UserRequest;
import com.richjun.campride.user.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getMyInfo(CustomOAuth2User customOAuth2User) {
        User user = userRepository.findBySocialLoginId(customOAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        return UserResponse.from(user);

    }

    public UserResponse updateMyInfo(CustomOAuth2User customOAuth2User, UserRequest userRequest) {
        User user = userRepository.findBySocialLoginId(customOAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        user.update(userRequest);
        return UserResponse.from(user);
    }
}
