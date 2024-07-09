package com.richjun.campride.global.auth.handler;


import static com.richjun.campride.global.jwt.util.CookieAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.jwt.JwtTokenProvider;
import com.richjun.campride.global.jwt.dto.TokenResponse;
import com.richjun.campride.global.jwt.util.CookieAuthorizationRequestRepository;
import com.richjun.campride.global.jwt.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${oauth.authorizedRedirectUri}")
    private String redirectUri;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

    private final JwtTokenProvider jwtTokenProvider;


    public CustomSuccessHandler(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper,
                                CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.cookieAuthorizationRequestRepository = cookieAuthorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info(authentication.getName());

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


    protected String determineTargetUrl(jakarta.servlet.http.HttpServletRequest request,
                                        jakarta.servlet.http.HttpServletResponse response,
                                        Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new RuntimeException("redirect URIs are not matched.");
        }
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        //JWT 생성
        TokenResponse tokenInfo = jwtTokenProvider.generateToken(authentication);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accesstoken", tokenInfo.getAccessToken())
                .queryParam("refreshtoken", tokenInfo.getRefreshToken())
                .build().toUriString();
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


