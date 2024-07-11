package com.richjun.campride.comment.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.richjun.campride.comment.request.CommentRequest;
import com.richjun.campride.comment.service.CommentService;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.post.request.PostRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    public ResponseEntity<Long> addComment(@AuthenticationPrincipal final CustomOAuth2User oAuth2User,
                                           @RequestBody @Valid final CommentRequest commentRequest) {

        return ResponseEntity.ok().body(commentService.addComment(oAuth2User, commentRequest));
    }


}
