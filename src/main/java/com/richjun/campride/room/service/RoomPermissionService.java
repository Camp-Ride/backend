package com.richjun.campride.room.service;

import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_USER_ID;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_ROOM_LEADER;

import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.global.exception.ExceptionCode;
import com.richjun.campride.room.domain.repository.RoomRepository;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
class RoomPermissionService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public Boolean isCreatedBy(Long roomId, CustomOAuth2User oAuth2User) {

        User user = userRepository.findBySocialLoginId(oAuth2User.getName()).orElseThrow(() -> new BadRequestException(
                NOT_FOUND_USER_ID));

        Boolean isCreatedBy = roomRepository.existsByIdAndLeaderNickname(roomId, user.getNickname());

        if (!isCreatedBy) {
            throw new BadRequestException(NOT_ROOM_LEADER);
        }

        return isCreatedBy;
    }


}