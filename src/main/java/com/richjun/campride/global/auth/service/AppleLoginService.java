package com.richjun.campride.global.auth.service;

import static com.richjun.campride.global.exception.ExceptionCode.FAIL_TO_LOGIN_WITH_APPLE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.global.auth.request.ApplePublicKey;
import com.richjun.campride.global.auth.response.ApplePublicKeyResponse;
import com.richjun.campride.global.auth.util.ApplePublicKeyGenerator;
import com.richjun.campride.global.exception.AuthException;
import com.richjun.campride.global.jwt.JwtTokenProvider;
import com.richjun.campride.global.jwt.dto.TokenResponse;
import com.richjun.campride.global.jwt.service.TokenService;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.service.UserService;
import io.jsonwebtoken.Claims;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.naming.AuthenticationException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AppleLoginService {

    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private final RestTemplate restTemplate;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final TokenService tokenService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AppleLoginService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper,
                             ApplePublicKeyGenerator applePublicKeyGenerator, TokenService tokenService,
                             UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.restTemplate = restTemplateBuilder.build();
        this.applePublicKeyGenerator = applePublicKeyGenerator;
        this.tokenService = tokenService;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public ApplePublicKeyResponse fetchApplePublicKeys() {
        try {
            return restTemplate.getForObject(APPLE_PUBLIC_KEYS_URL, ApplePublicKeyResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching Apple public keys", e);
        }
    }


    public TokenResponse loginWithApple(String identity_Token, String deviceToken) {

        try {
            Map<String, String> headers = tokenService.parseHeaders(identity_Token);

            // 2. Apple의 공개 키 가져오기
            ApplePublicKeyResponse applePublicKeyResponse = fetchApplePublicKeys();

            // 3. 공개 키 생성
            PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, applePublicKeyResponse);

            // 4. ID 토큰 검증 및 클레임 추출
            Claims claims = tokenService.getTokenClaims(identity_Token, publicKey);

            // 5. 사용자 정보 추출
            String appleUserId = claims.getSubject();
            String email = claims.get("email", String.class);

            // 6. 사용자 찾기 또는 생성
            User user = userService.findOrCreateUser(appleUserId, email, "ROLE_USER", deviceToken);

            // 7. 자체 토큰 생성
            String accessToken = jwtTokenProvider.generateAccessToken(user.getSocialLoginId(), user.getRole());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getSocialLoginId(), user.getRole());

            // 8. 토큰 응답 반환
            return new TokenResponse(accessToken, refreshToken);
        } catch (AuthenticationException e) {
            throw new AuthException(FAIL_TO_LOGIN_WITH_APPLE);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


}
