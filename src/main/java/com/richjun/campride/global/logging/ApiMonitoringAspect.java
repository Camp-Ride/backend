package com.richjun.campride.global.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ApiMonitoringAspect {

    private final CloudWatchLogsService cloudWatchLogsService;
    private static final String LOG_FORMAT =
            "API: {} | Method: {} | Time: {}ms | Success: {} | Error: {}";

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object monitorApi(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String apiName = joinPoint.getSignature().getName();
        String method = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getMethod();

        try {
            Object result = joinPoint.proceed();
            String logMessage = String.format("API: %s | Method: %s | Time: %dms | Success: %s | Error: %s",
                    apiName, method, System.currentTimeMillis() - startTime, true, "-");

            log.info(LOG_FORMAT,
                    apiName,
                    method,
                    System.currentTimeMillis() - startTime,
                    true,
                    "-"
            );
            cloudWatchLogsService.sendLog(logMessage);
            return result;

        } catch (Exception e) {
            String logMessage = String.format("API: %s | Method: %s | Time: %dms | Success: %s | Error: %s",
                    apiName, method, System.currentTimeMillis() - startTime, false, e.getMessage());

            log.error(LOG_FORMAT,
                    apiName,
                    method,
                    System.currentTimeMillis() - startTime,
                    false,
                    e.getMessage()
            );
            cloudWatchLogsService.sendLog(logMessage);
            throw e;
        }
    }
}