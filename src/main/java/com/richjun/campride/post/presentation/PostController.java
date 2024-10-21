package com.richjun.campride.post.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.post.request.PostRequest;
import com.richjun.campride.post.response.PostResponse;
import com.richjun.campride.post.service.PostService;
import com.richjun.campride.room.request.RoomRequest;
import com.richjun.campride.room.response.RoomResponse;
import com.richjun.campride.room.service.RoomService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;
    private final ObjectMapper objectMapper;

    @PostMapping("/v1/post")
    public ResponseEntity<Long> createPost(@AuthenticationPrincipal final CustomOAuth2User oAuth2User,
                                           @RequestPart("postRequest") @Valid final String postRequestStr,
                                           @RequestPart(value = "images", required = false) final List<MultipartFile> images)
            throws JsonProcessingException {

        PostRequest postRequest = objectMapper.readValue(postRequestStr, PostRequest.class);

        return ResponseEntity.ok().body(postService.create(oAuth2User, postRequest, images));
    }

    @GetMapping("/v1/post/paging")
    public ResponseEntity<Page<PostResponse>> searchPostsPage(
            @RequestParam(value = "sortType", required = false) String sortType, Pageable pageable) {
        return ResponseEntity.ok().body(postService.searchPostsPage(pageable, sortType));
    }

    @GetMapping("/v2/post/paging")
    public ResponseEntity<Page<PostResponse>> searchPostsPageV2(
            @AuthenticationPrincipal final CustomOAuth2User oAuth2User,
            @RequestParam(value = "sortType", required = false) String sortType, Pageable pageable) {
        return ResponseEntity.ok().body(postService.searchPostsPageV2(oAuth2User, pageable, sortType));
    }

    @GetMapping("/v3/post/paging")
    public ResponseEntity<Page<PostResponse>> searchPostsPageV3(
            @AuthenticationPrincipal final CustomOAuth2User oAuth2User,
            @RequestParam(value = "sortType", required = false) String sortType, Pageable pageable) {
        return ResponseEntity.ok().body(postService.searchPostsPageV3(oAuth2User, pageable, sortType));
    }


    @GetMapping("/v1/post/{id}")
    public ResponseEntity<PostResponse> getPost(@AuthenticationPrincipal final CustomOAuth2User oAuth2User,
                                                @PathVariable Long id) {
        return ResponseEntity.ok().body(postService.getPost(id));
    }


    @DeleteMapping("/v1/post/{id}")
    @PreAuthorize("@postPermissionService.isCreatedBy(#id, #oAuth2User)")
    public ResponseEntity<Long> deletePost(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                           @PathVariable Long id) {
        return ResponseEntity.ok().body(postService.deletePost(id));
    }


    @PutMapping("/v1/post/{id}")
    @PreAuthorize("@postPermissionService.isCreatedBy(#id, #oAuth2User)")
    public ResponseEntity<Long> updatePost(@AuthenticationPrincipal final CustomOAuth2User oAuth2User,
                                           @RequestPart("postRequest") @Valid final String postRequestStr,
                                           @RequestPart(value = "images", required = false) final List<MultipartFile> images,
                                           @PathVariable final Long id) throws JsonProcessingException {
        PostRequest postRequest = objectMapper.readValue(postRequestStr, PostRequest.class);

        return ResponseEntity.ok().body(postService.updatePost(id, postRequest, images));
    }


}
