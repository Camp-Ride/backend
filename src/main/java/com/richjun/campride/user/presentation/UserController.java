package com.richjun.campride.user.presentation;

import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.room.response.RoomResponse;
import com.richjun.campride.user.request.UserRequest;
import com.richjun.campride.user.response.UserResponse;
import com.richjun.campride.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class UserController {

    private final UserService userService;


    @GetMapping("/user")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal final CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok().body(userService.getMyInfo(oAuth2User));
    }

    @PutMapping("/user")
    public ResponseEntity<UserResponse> updateMyInfo(@AuthenticationPrincipal final CustomOAuth2User oAuth2User,
                                                     @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok().body(userService.updateMyInfo(oAuth2User,userRequest));
    }


}
