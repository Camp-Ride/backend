package com.richjun.campride.report.presentation;


import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.report.request.ReportRequest;
import com.richjun.campride.report.service.ReportService;
import com.richjun.campride.room.request.RoomRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/report")
    public ResponseEntity<Long> createReport(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                             @RequestBody @Valid final ReportRequest reportRequest) {

        return ResponseEntity.ok().body(reportService.create(reportRequest, oAuth2User));
    }


}
