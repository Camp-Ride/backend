package com.richjun.campride.room.response;

import com.richjun.campride.global.location.domain.Location;
import com.richjun.campride.room.domain.Room;
import com.richjun.campride.room.domain.type.RoomType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomJoinedResponse {

    private Long id;
    private String leaderNickname;
    private String title;
    private String departure;
    private Location departureLocation;
    private String destination;
    private Location destinationLocation;
    private LocalDateTime departureTime;
    private int trainingDays;
    private int maxParticipants;
    private RoomType roomType;
    private List<ParticipantResponse> participants;
    private LocalDateTime createdAt;
    private LatestMessageResponse latestMessageResponse;
    private Long unreadMessageCount;


    public static RoomJoinedResponse from(Room room, LatestMessageResponse latestMessageResponse,
                                          Long unreadMessageCount) {
        return new RoomJoinedResponse(room.getId(),
                room.getLeaderNickname(),
                room.getTitle(),
                room.getDeparture(),
                room.getDepartureLocation(),
                room.getDestination(),
                room.getDestinationLocation(),
                room.getDepartureTime(),
                room.getTrainingDays(),
                room.getMaxParticipants(),
                room.getRoomType(),
                ParticipantResponse.from(room.getParticipants()),
                room.getCreatedAt(),
                latestMessageResponse,
                unreadMessageCount
        );

    }


}