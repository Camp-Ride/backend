package com.richjun.campride.global.auth.handler;


import static com.richjun.campride.global.exception.ExceptionCode.EXPIRED_REFRESH_TOKEN;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_REFRESH_TOKEN;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_USER_ID;
import static com.richjun.campride.global.jwt.util.CookieAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.exception.AuthException;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.global.exception.ExceptionCode;
import com.richjun.campride.global.jwt.JwtTokenProvider;
import com.richjun.campride.global.jwt.domain.RefreshToken;
import com.richjun.campride.global.jwt.domain.repository.RefreshTokenRepository;
import com.richjun.campride.global.jwt.dto.TokenResponse;
import com.richjun.campride.global.jwt.util.CookieAuthorizationRequestRepository;
import com.richjun.campride.global.jwt.util.CookieUtils;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${oauth.authorizedRedirectUri}")
    private String redirectUri;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

    private final JwtTokenProvider jwtTokenProvider;


    public CustomSuccessHandler(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper,
                                CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository,
                                RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.cookieAuthorizationRequestRepository = cookieAuthorizationRequestRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.info("Response has already been committed.");
            return;
        }
        clearAuthenticationAttributes(request, response);

        try {
            getRedirectStrategy().sendRedirect(request, response, "campride:" + targetUrl);
            log.info("redirect success : " + targetUrl);
        } catch (Exception e) {
            log.info("redirect failed : " + e);
        }

    }


    @Transactional
    protected String determineTargetUrl(jakarta.servlet.http.HttpServletRequest request,
                                        jakarta.servlet.http.HttpServletResponse response,
                                        Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new RuntimeException("redirect URIs are not matched.");
        }
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        User user = userRepository.findBySocialLoginId(authentication.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        Boolean isNicknameUpdated = user.getIsNicknameUpdated() ? true : false;

        String accessToken = jwtTokenProvider.generateAccessToken(authentication.getName(),
                authentication.getAuthorities());

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .map(this::verifyExpirationAndUpdateToken)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_REFRESH_TOKEN));

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accesstoken", accessToken)
                .queryParam("refreshtoken", refreshToken.getToken())
                .queryParam("isNicknameUpdated", isNicknameUpdated)
                .build().toUriString();
    }

    private RefreshToken verifyExpirationAndUpdateToken(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            log.info("refreshToken expired : {}", refreshToken.getToken());
            refreshToken.updateToken();
            return refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    protected void clearAuthenticationAttributes(jakarta.servlet.http.HttpServletRequest request,
                                                 jakarta.servlet.http.HttpServletResponse response) {
        super.clearAuthenticationAttributes((jakarta.servlet.http.HttpServletRequest) request);
        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        URI authorizedUri = URI.create(redirectUri);

        if (authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && authorizedUri.getPort() == clientRedirectUri.getPort()) {
            return true;
        }
        return false;
    }
//

}


