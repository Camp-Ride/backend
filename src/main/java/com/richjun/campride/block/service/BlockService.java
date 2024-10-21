package com.richjun.campride.block.service;

import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_USER_ID;

import com.richjun.campride.block.domain.Block;
import com.richjun.campride.block.domain.repository.BlockRepository;
import com.richjun.campride.block.request.BlockRequest;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BlockService {

    private final UserRepository userRepository;
    private final BlockRepository blockRepository;

    public Long create(BlockRequest reportRequest, CustomOAuth2User oAuth2User) {

        User user = userRepository.findBySocialLoginId(oAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        User targetUser = userRepository.findById(reportRequest.getTargetUserId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        Block block = Block.of(user, targetUser);

        return blockRepository.save(block).getId();
    }


}
