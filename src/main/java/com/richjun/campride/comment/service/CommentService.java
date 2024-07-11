package com.richjun.campride.comment.service;

import static com.richjun.campride.global.exception.ExceptionCode.*;

import com.richjun.campride.comment.domain.Comment;
import com.richjun.campride.comment.repository.CommentRepository;
import com.richjun.campride.comment.request.CommentRequest;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.global.exception.ExceptionCode;
import com.richjun.campride.post.domain.Post;
import com.richjun.campride.post.domain.repository.PostRepository;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Long addComment(final CustomOAuth2User oAuth2User, final CommentRequest commentRequest) {

        User user = userRepository.findBySocialLoginId(oAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        Post post = postRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_POST_ID));

        Comment comment = Comment.of(commentRequest, user.getNickname(), post);
        commentRepository.save(comment);

        return comment.getId();
    }
}
