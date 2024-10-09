package com.richjun.campride.global.auth.service;

import static com.richjun.campride.global.exception.ExceptionCode.FAIL_TO_GENERATE_RANDOM_NICKNAME;

import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.auth.response.GoogleResponse;
import com.richjun.campride.global.auth.response.KakaoResponse;
import com.richjun.campride.global.auth.response.NaverResponse;
import com.richjun.campride.global.auth.response.OAuth2Response;
import com.richjun.campride.global.auth.response.OAuth2UserResponse;
import com.richjun.campride.global.exception.AuthException;
import com.richjun.campride.global.jwt.domain.repository.RefreshTokenRepository;
import com.richjun.campride.global.jwt.service.TokenService;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final int MAX_TRY_COUNT = 5;
    private static final int FOUR_DIGIT_RANGE = 10000;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;

    public CustomOAuth2UserService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                                   TokenService tokenService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenService = tokenService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String nickname = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();

        OAuth2UserResponse oAuth2UserResponse = new OAuth2UserResponse(oAuth2Response.getName(), nickname, "ROLE_USER");

        String deviceToken = getDeviceTokenFromSession();

        findOrCreateUser(oAuth2UserResponse.getNickname(), oAuth2UserResponse.getUsername(),
                oAuth2UserResponse.getRole(), deviceToken);

        return new CustomOAuth2User(oAuth2UserResponse);

    }

    private String getDeviceTokenFromSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        String deviceToken = (String) session.getAttribute("deviceToken");
        session.removeAttribute("deviceToken");
        return deviceToken;
    }

    private User findOrCreateUser(final String socialLoginId, final String name, String role, String deviceToken) {
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

}