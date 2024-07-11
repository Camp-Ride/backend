package com.richjun.campride.like.presentation;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.like.request.LikeRequest;
import com.richjun.campride.like.service.LikeService;
import com.richjun.campride.post.request.PostRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LikeController {


    private final LikeService likeService;

    @PostMapping("/like/{id}")
    public ResponseEntity<Long> like(@AuthenticationPrincipal final CustomOAuth2User oAuth2User,
                                     @PathVariable final Long id,
                                     @RequestBody @Valid final LikeRequest likeRequest) {

        return ResponseEntity.ok().body(likeService.like(oAuth2User, id, likeRequest));
    }

    @DeleteMapping("/unlike/{id}")
    @PreAuthorize("@likePermissionService.isCreatedBy(#id, #oAuth2User)")
    public ResponseEntity<Long> unLike(@AuthenticationPrincipal final CustomOAuth2User oAuth2User,
                                       @PathVariable final Long id) {

        return ResponseEntity.ok().body(likeService.unLike(id));
    }


}
