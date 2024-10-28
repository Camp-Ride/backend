package com.richjun.campride.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.global.exception.AuthException;
import com.richjun.campride.global.exception.ErrorResponse;
import com.richjun.campride.global.exception.ExceptionCode;
import com.richjun.campride.global.logging.CloudWatchLogsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor // 생성자 주입을 위해 추가
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

    private static final String LOG_FORMAT = "API: {} | Method: {} | Time: {}ms | Success: {} | Error: {}";
    private final CloudWatchLogsService cloudWatchLogsService; // CloudWatch 서비스 주입

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
            String logMessage = String.format("API: %s | Method: %s | Time: %dms | Success: %s | Error: %s",
                    request.getRequestURI(),
                    request.getMethod(),
                    System.currentTimeMillis() - startTime,
                    true,
                    "-"
            );

            log.info(LOG_FORMAT,
                    request.getRequestURI(),
                    request.getMethod(),
                    System.currentTimeMillis() - startTime,
                    true,
                    "-"
            );
            cloudWatchLogsService.sendLog(logMessage);

        } catch (AuthException e) {
            String logMessage = String.format("API: %s | Method: %s | Time: %dms | Success: %s | Error: %s",
                    request.getRequestURI(),
                    request.getMethod(),
                    System.currentTimeMillis() - startTime,
                    false,
                    e.getMessage()
            );

            log.error(LOG_FORMAT,
                    request.getRequestURI(),
                    request.getMethod(),
                    System.currentTimeMillis() - startTime,
                    false,
                    e.getMessage()
            );
            cloudWatchLogsService.sendLog(logMessage);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");

            try {
                String json = new ObjectMapper().writeValueAsString(
                        new ErrorResponse(ExceptionCode.INVALID_ACCESS_TOKEN.getCode(),
                                ExceptionCode.INVALID_ACCESS_TOKEN.getMessage()));
                response.getWriter().write(json);
            } catch (Exception ex) {
                log.error("Error writing response: {}", ex.getMessage());
            }
        }
    }
}