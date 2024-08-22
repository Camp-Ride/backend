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


    private Long id;
    private String socialLoginId;
    private String nickname;
    private String role;
    private Long lastSeenMessageScore;


    public static List<ParticipantResponse> from(List<Participant> participants) {

        return participants.stream()
                .map((participant) -> new ParticipantResponse(participant.getUser().getId(),
                        participant.getUser().getSocialLoginId(), participant.getUser().getNickname(),
                        participant.getUser().getRole(), participant.getLastSeenMessageScore())).collect(
                        Collectors.toList());
    }


}
