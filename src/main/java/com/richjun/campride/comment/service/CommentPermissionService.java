package com.richjun.campride.comment.service;

import static com.richjun.campride.global.exception.ExceptionCode.NOT_COMMENT_AUTHOR;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_USER_ID;

import com.richjun.campride.comment.domain.repository.CommentRepository;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
class CommentPermissionService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public Boolean isCreatedBy(Long postId, CustomOAuth2User oAuth2User) {

        User user = userRepository.findBySocialLoginId(oAuth2User.getName()).orElseThrow(() -> new BadRequestException(
                NOT_FOUND_USER_ID));

        Boolean isCreatedBy = commentRepository.existsByIdAndAuthor(postId, user.getNickname());

        if (!isCreatedBy) {
            throw new BadRequestException(NOT_COMMENT_AUTHOR);
        }

        return isCreatedBy;
    }


}