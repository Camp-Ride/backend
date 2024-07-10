package com.richjun.campride.like.response;

import com.richjun.campride.like.domain.Like;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeResponse {
    private Long id;
    private String nickname;

    public static List<LikeResponse> from(List<Like> likes) {

        return likes.stream()
                .map((like) -> new LikeResponse(like.getId(), like.getUsername())).collect(Collectors.toList());
    }
}
