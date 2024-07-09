package com.richjun.campride.room.response;

import com.richjun.campride.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class ParticipantResponse {


    Long id;
    String socialLoginId;
    String nickname;
    String role;


    public static List<ParticipantResponse> from(List<User> users) {

        return users.stream()
                .map((user) -> new ParticipantResponse(user.getId(), user.getSocialLoginId(), user.getNickname(),
                        user.getRole())).collect(
                        Collectors.toList());
    }


}
