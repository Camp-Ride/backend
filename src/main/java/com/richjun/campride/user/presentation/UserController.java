package com.richjun.campride.user.presentation;

import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.auth.request.AppleLoginRequest;
import com.richjun.campride.global.auth.service.AppleLoginService;
import com.richjun.campride.global.jwt.dto.TokenAppleLoginResponse;
import com.richjun.campride.global.jwt.dto.TokenResponse;
import com.richjun.campride.user.request.UserRequest;
import com.richjun.campride.user.response.UserResponse;
import com.richjun.campride.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller
@Slf4j
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

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal final CustomOAuth2User oAuth2User) {
        userService.deleteUser(oAuth2User);
        return ResponseEntity.ok().body("User deleted successfully");
    }

    @GetMapping("/test/session-check")
    public Map<String, Object> checkSession(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> info = new HashMap<>();

        // 현재 세션 존재 여부
        HttpSession session = request.getSession(false);
        info.put("hasSession", session != null);

        // 요청에 포함된 모든 쿠키 정보
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            info.put("cookies", Arrays.stream(cookies)
                    .map(c -> c.getName() + "=" + c.getValue())
                    .collect(Collectors.toList()));
        }

        // 현재 시간
        info.put("timestamp", new Date());

        // URL에 jsessionid 포함 여부
        info.put("requestURL", request.getRequestURL().toString());

        log.info("Session check: {}", info);

        return info;
    }


}
