package com.richjun.campride.post.service;

import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_USER_ID;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_POST_AUTHOR;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_ROOM_LEADER;

import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.post.domain.repository.PostRepository;
import com.richjun.campride.room.domain.repository.RoomRepository;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
class PostPermissionService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Boolean isCreatedBy(Long postId, CustomOAuth2User oAuth2User) {

        User user = userRepository.findBySocialLoginId(oAuth2User.getName()).orElseThrow(() -> new BadRequestException(
                NOT_FOUND_USER_ID));

        Boolean isCreatedBy = postRepository.existsByIdAndNickname(postId, user.getNickname());

        if (!isCreatedBy) {
            throw new BadRequestException(NOT_POST_AUTHOR);
        }

        return isCreatedBy;
    }


}