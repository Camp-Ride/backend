package com.richjun.campride.user.service;

import static com.richjun.campride.global.exception.ExceptionCode.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.auth.response.ApplePublicKeyResponse;
import com.richjun.campride.global.auth.service.AppleLoginService;
import com.richjun.campride.global.exception.AuthException;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.global.exception.ExceptionCode;
import com.richjun.campride.global.jwt.domain.repository.RefreshTokenRepository;
import com.richjun.campride.global.jwt.dto.TokenResponse;
import com.richjun.campride.global.jwt.service.TokenService;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import com.richjun.campride.user.request.UserRequest;
import com.richjun.campride.user.response.UserResponse;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.naming.AuthenticationException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private static final int MAX_TRY_COUNT = 5;
    private static final int FOUR_DIGIT_RANGE = 10000;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;

    public UserResponse getMyInfo(CustomOAuth2User customOAuth2User) {
        User user = userRepository.findBySocialLoginId(customOAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        return UserResponse.from(user);

    }

    public UserResponse updateMyInfo(CustomOAuth2User customOAuth2User, UserRequest userRequest) {
        User user = userRepository.findBySocialLoginId(customOAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        if (userRepository.existsByNicknameAndIdNot(userRequest.getNickname(), user.getId())) {
            throw new BadRequestException(DUPLICATED_NICKNAME);
        }

        user.update(userRequest);
        return UserResponse.from(user);
    }

    public String getUserFCMToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        return user.getDeviceToken();
    }

    public User findOrCreateUser(final String socialLoginId, final String name, String role, String deviceToken) {
        return userRepository.findBySocialLoginId(socialLoginId)
                .orElseGet(() -> createUser(socialLoginId, name, role, deviceToken));
    }

    private User createUser(final String socialLoginId, final String name, String role, String deviceToken) {
        int tryCount = 0;
        while (tryCount < MAX_TRY_COUNT) {
            final String nicknameWithRandomNumber = name + generateRandomFourDigitCode();
            if (!userRepository.existsBySocialLoginId(nicknameWithRandomNumber)) {
                User user = userRepository.save(new User(socialLoginId, nicknameWithRandomNumber, role, deviceToken));
                refreshTokenRepository.save(tokenService.createRefreshToken(user.getId()));
                return user;
            }
            tryCount += 1;
        }
        throw new AuthException(FAIL_TO_GENERATE_RANDOM_NICKNAME);
    }

    private String generateRandomFourDigitCode() {
        final int randomNumber = (int) (Math.random() * FOUR_DIGIT_RANGE);
        return String.format("%04d", randomNumber);
    }

    public void deleteUser(CustomOAuth2User oAuth2User) {
        User user = userRepository.findBySocialLoginId(oAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        userRepository.delete(user);
    }
}
