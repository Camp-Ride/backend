package com.richjun.campride.block.presentation;


import com.richjun.campride.block.request.BlockRequest;
import com.richjun.campride.block.service.BlockService;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
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
public class BlockController {

    private final BlockService blockService;

    @PostMapping("/block")
    public ResponseEntity<Long> createReport(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                             @RequestBody @Valid final BlockRequest blockRequest) {

        return ResponseEntity.ok().body(blockService.create(blockRequest, oAuth2User));
    }


}
