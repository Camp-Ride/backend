package com.richjun.campride.like.service;

import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_USER_ID;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_LIKE_MASTER;
import static com.richjun.campride.like.domain.type.ContentType.COMMENT;
import static com.richjun.campride.like.domain.type.ContentType.POST;

import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.like.domain.repository.LikeRepository;
import com.richjun.campride.like.domain.type.ContentType;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
class LikePermissionService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    public Boolean isCreatedBy(Long id, ContentType contentType, CustomOAuth2User oAuth2User) {

        User user = userRepository.findBySocialLoginId(oAuth2User.getName()).orElseThrow(() -> new BadRequestException(
                NOT_FOUND_USER_ID));

        Boolean isCreatedBy = false;

        log.info(id.toString());
        log.info(user.getNickname());

        if (contentType.equals(POST) && likeRepository.existsByPostIdAndNickname(id, user.getNickname())) {
            isCreatedBy = true;
            log.info("existsByPostIdAndNickname");
        }
        if (contentType.equals(COMMENT) && likeRepository.existsByCommentIdAndNickname(id, user.getNickname())) {
            isCreatedBy = true;
            log.info("existsByCommentIdAndNickname");
        }

        if (!isCreatedBy) {
            throw new BadRequestException(NOT_LIKE_MASTER);
        }

        return isCreatedBy;
    }


}