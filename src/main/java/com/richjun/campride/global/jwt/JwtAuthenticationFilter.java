package com.richjun.campride.global.jwt;

import static com.richjun.campride.global.exception.ExceptionCode.INVALID_ACCESS_TOKEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.global.exception.ErrorResponse;
import com.richjun.campride.global.exception.ExceptionCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            jakarta.servlet.FilterChain chain) throws jakarta.servlet.ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);

        log.info("token : {}", token);
        log.info("request.getRequestURI() : {}", request.getRequestURI());
        log.info("request.getRequestURL() : {}", request.getRequestURL());
        log.info("request.getQueryString() : {}", request.getQueryString());

        HttpSession session = request.getSession(false);
        if (session != null) {
            log.info("현재 세션 정보 - ID: {}, 생성시간: {}, 마지막접근: {}, 만료시간: {}",
                    session.getId(),
                    new Date(session.getCreationTime()),
                    new Date(session.getLastAccessedTime()),
                    session.getMaxInactiveInterval());
        }

        if (jwtTokenProvider.validateToken(token)) {

            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        chain.doFilter(request, response);
    }


}