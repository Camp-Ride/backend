package com.richjun.campride.room.response;

import com.richjun.campride.room.domain.Participant;
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


    public static List<ParticipantResponse> from(List<Participant> participants) {

        return participants.stream()
                .map((participant) -> new ParticipantResponse(participant.getUser().getId(),
                        participant.getUser().getSocialLoginId(), participant.getUser().getNickname(),
                        participant.getUser().getRole())).collect(
                        Collectors.toList());
    }


}
