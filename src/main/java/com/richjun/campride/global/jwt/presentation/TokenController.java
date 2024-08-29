package com.richjun.campride.global.jwt.presentation;

import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.jwt.dto.TokenRefreshRequest;
import com.richjun.campride.global.jwt.dto.TokenResponse;
import com.richjun.campride.global.jwt.service.TokenService;
import com.richjun.campride.user.domain.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenResponse> refreshtoken(@AuthenticationPrincipal final CustomOAuth2User oAuth2User,
                                                      @Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
        return ResponseEntity.ok().body(tokenService.refreshToken(oAuth2User, tokenRefreshRequest));
    }

}
