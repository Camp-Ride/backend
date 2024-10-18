package com.richjun.campride.user.presentation;

import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.auth.request.AppleLoginRequest;
import com.richjun.campride.global.auth.service.AppleLoginService;
import com.richjun.campride.global.jwt.dto.TokenAppleLoginResponse;
import com.richjun.campride.global.jwt.dto.TokenResponse;
import com.richjun.campride.user.request.UserRequest;
import com.richjun.campride.user.response.UserResponse;
import com.richjun.campride.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller
@RequiredArgsConstructor
@SessionAttributes("deviceToken")
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final AppleLoginService appleLoginService;


    @GetMapping("/user")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal final CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok().body(userService.getMyInfo(oAuth2User));
    }

    @PutMapping("/user")
    public ResponseEntity<UserResponse> updateMyInfo(@AuthenticationPrincipal final CustomOAuth2User oAuth2User,
                                                     @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok().body(userService.updateMyInfo(oAuth2User, userRequest));
    }

    @GetMapping("/login")
    public String login(@RequestParam String provider, @RequestParam String deviceToken, Model model) {
        model.addAttribute("deviceToken", deviceToken);
        return "redirect:/oauth2/authorization/" + provider;
    }

    @GetMapping("/login/apple")
    public ResponseEntity<TokenAppleLoginResponse> loginWithApple(@RequestBody AppleLoginRequest appleLoginRequest) {
        return ResponseEntity.ok().body(appleLoginService.loginWithApple(appleLoginRequest.getIdentityToken(),
                appleLoginRequest.getDeviceToken()));
    }

}
