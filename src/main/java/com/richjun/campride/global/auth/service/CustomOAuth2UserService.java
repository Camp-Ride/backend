package com.richjun.campride.global.auth.service;

import static com.richjun.campride.global.exception.ExceptionCode.FAIL_TO_GENERATE_RANDOM_NICKNAME;

import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.auth.response.GoogleResponse;
import com.richjun.campride.global.auth.response.KakaoResponse;
import com.richjun.campride.global.auth.response.NaverResponse;
import com.richjun.campride.global.auth.response.OAuth2Response;
import com.richjun.campride.global.auth.response.OAuth2UserResponse;
import com.richjun.campride.global.exception.AuthException;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final int MAX_TRY_COUNT = 5;
    private static final int FOUR_DIGIT_RANGE = 10000;

    final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

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

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();

        OAuth2UserResponse oAuth2UserResponse = new OAuth2UserResponse(username, oAuth2Response.getName(), "ROLE_USER");

        findOrCreateMember(oAuth2UserResponse.getUsername(), oAuth2UserResponse.getName(),
                oAuth2UserResponse.getRole());

        return new CustomOAuth2User(oAuth2UserResponse);

    }

    private User findOrCreateMember(final String socialLoginId, final String nickname, String role) {
        return userRepository.findBySocialLoginId(socialLoginId)
                .orElseGet(() -> createUser(socialLoginId, nickname, role));
    }


    private User createUser(final String socialLoginId, final String nickname, String role) {
        int tryCount = 0;
        while (tryCount < MAX_TRY_COUNT) {
            final String nicknameWithRandomNumber = nickname + generateRandomFourDigitCode();
            if (!userRepository.existsBySocialLoginId(nicknameWithRandomNumber)) {
                return userRepository.save(new User(socialLoginId, nicknameWithRandomNumber, role));
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