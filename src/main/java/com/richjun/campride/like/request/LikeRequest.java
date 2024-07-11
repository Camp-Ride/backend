package com.richjun.campride.like.request;

import com.richjun.campride.like.domain.LikeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LikeRequest {

    @NotNull(message = "좋아요 타입을 입력해 주세요.")
    private LikeType likeType;

}
