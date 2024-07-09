package com.richjun.campride.room.response;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.richjun.campride.global.location.domain.Location;
import com.richjun.campride.room.domain.Room;
import com.richjun.campride.room.domain.type.RoomType;
import com.richjun.campride.user.domain.User;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomResponse {

    private Long id;
    private String leaderNickname;
    private String title;
    private String departure;
    private Location departureLocation;
    private String destination;
    private Location destinationLocation;
    private LocalDateTime departureTime;
    private int maxParticipants;
    private RoomType roomType;
    private List<ParticipantResponse> participants;


    public static RoomResponse from(Room room) {

        return new RoomResponse(room.getId(),
                room.getLeader().getNickname(),
                room.getTitle(),
                room.getDeparture(),
                room.getDepartureLocation(),
                room.getDestination(),
                room.getDestinationLocation(),
                room.getDepartureTime(),
                room.getMaxParticipants(),
                room.getRoomType(),
                ParticipantResponse.from(room.getParticipants()));
    }


}
