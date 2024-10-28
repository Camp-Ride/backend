package com.richjun.campride.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.global.exception.AuthException;
import com.richjun.campride.global.exception.ErrorResponse;
import com.richjun.campride.global.exception.ExceptionCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (AuthException e) {

//            response.setStatus(ExceptionCode.INVALID_ACCESS_TOKEN.getCode());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");

            try {
                String json = new ObjectMapper().writeValueAsString(
                        new ErrorResponse(ExceptionCode.INVALID_ACCESS_TOKEN.getCode(),
                                ExceptionCode.INVALID_ACCESS_TOKEN.getMessage()));
                response.getWriter().write(json);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }

        }
    }


}