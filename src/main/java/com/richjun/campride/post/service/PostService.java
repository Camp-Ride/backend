package com.richjun.campride.post.service;

import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_USER_ID;
import static java.lang.Enum.valueOf;

import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.global.exception.ExceptionCode;
import com.richjun.campride.image.response.ImagesResponse;
import com.richjun.campride.image.service.ImageService;
import com.richjun.campride.post.domain.Post;
import com.richjun.campride.post.domain.repository.PostRepository;
import com.richjun.campride.post.request.PostRequest;
import com.richjun.campride.post.response.PostResponse;
import com.richjun.campride.room.request.RoomRequest;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PostService {


    private final PostRepository postRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;


    public Long create(final CustomOAuth2User oAuth2User, final PostRequest postRequest,
                       final List<MultipartFile> images) {

        User user = userRepository.findBySocialLoginId(oAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        ImagesResponse imagesResponse = imageService.save(images);

        Post post = Post.of(postRequest, user.getNickname(), imagesResponse);

        return postRepository.save(post).getId();
    }

    public Page<PostResponse> searchPostsPage(Pageable pageable) {
        return postRepository.searchPostsPage(pageable);
    }


    public PostResponse getPost(final Long id) {

        Post post = postRepository.findById(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        return PostResponse.of(post);
    }

    public Long deletePost(final Long id) {

        Post post = postRepository.findById(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));
        postRepository.delete(post);

        return post.getId();
    }


    public Long updatePost(final Long id, final PostRequest postRequest, final List<MultipartFile> images) {

        Post post = postRepository.findById(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));
        ImagesResponse imagesResponse = imageService.save(images);

        List<String> imageNames = imagesResponse.getImageNames();
        if (imageNames != null && !imageNames.isEmpty()) {
            postRequest.getImageNames().addAll(imageNames);
        }

        post.update(postRequest.getTitle(), postRequest.getContents(), postRequest.getImageNames());

        return post.getId();
    }
}
