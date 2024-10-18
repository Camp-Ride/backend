package com.richjun.campride.global.jwt.service;

import static com.richjun.campride.global.exception.ExceptionCode.EXPIRED_REFRESH_TOKEN;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_REFRESH_TOKEN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.global.exception.AuthException;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.global.exception.ExceptionCode;
import com.richjun.campride.global.jwt.JwtTokenProvider;
import com.richjun.campride.global.jwt.domain.RefreshToken;
import com.richjun.campride.global.jwt.domain.repository.RefreshTokenRepository;
import com.richjun.campride.global.jwt.dto.TokenRefreshRequest;
import com.richjun.campride.global.jwt.dto.TokenResponse;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
//    private Long refreshTokenDurationMs;

    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;     //7일


    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRE_TIME));
        refreshToken.setToken(UUID.randomUUID().toString());

        System.out.println("createRefreshToken : " + refreshToken.getToken());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }


    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new AuthException(EXPIRED_REFRESH_TOKEN);
        }

        return token;
    }

    @Transactional
    public Long deleteByUserId(User user) {
        return refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public TokenResponse refreshToken(TokenRefreshRequest tokenRefreshRequest) {
        // jwt가 만료되었기 때문에 refreshToken 요청을 보내는것이다.
        // 제일 먼저 refreshToken이 유효한지 확인
        // 유효하다면 -> accesstoken, refreshtoken 재발급 후 전송
        // 유효하지않다면 -> 유효하지 않다는 메세지와 재로그인 에러 전송

        RefreshToken refreshToken = findByToken(tokenRefreshRequest.getRefreshToken())
                .map(this::verifyExpiration)
                .orElseThrow(() -> new AuthException(NOT_FOUND_REFRESH_TOKEN));

        User user = refreshToken.getUser();

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getSocialLoginId(), user.getRole());

        refreshToken.updateToken();

        return new TokenResponse(newAccessToken, refreshToken.getToken());
    }



    public Map<String, String> parseHeaders(String token) throws JsonProcessingException {
        String header = token.split("\\.")[0];
        return new ObjectMapper().readValue(decodeHeader(header), Map.class);
    }

    public String decodeHeader(String token) {
        return new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
    }

    public Claims getTokenClaims(String token, PublicKey publicKey) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }




}