package com.richjun.campride.room.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.richjun.campride.global.location.domain.Location;
import com.richjun.campride.room.domain.type.RoomType;
import com.richjun.campride.room.request.RoomRequest;
import com.richjun.campride.user.domain.User;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Room {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String leaderNickname;


    @Column(nullable = false)
    private String departure;

    @Column(nullable = false)
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "departure_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "departure_longitude"))
    })
    private Location departureLocation;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "destination_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "destination_longitude"))
    })
    private Location destinationLocation;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private int maxParticipants;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @ManyToMany
    @JoinTable(
            name = "user_room",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants;


    public static Room of(final RoomRequest roomRequest, List<User> participants, String leaderNickname,
                          Location departureLocation,
                          Location destinationLocation) {
        return new Room(
                null,
                roomRequest.getTitle(),
                leaderNickname,
                roomRequest.getDeparture(),
                departureLocation,
                roomRequest.getDestination(),
                destinationLocation,
                roomRequest.getDepartureTime(),
                roomRequest.getMaxParticipants(),
                roomRequest.getRoomType(),
                participants
        );
    }

    public void update(RoomRequest roomRequest, Location departureLocation, Location destinationLocation) {
        this.title = roomRequest.getTitle();
        this.departure = roomRequest.getDeparture();
        this.departureLocation = departureLocation;
        this.destination = roomRequest.getDestination();
        this.destinationLocation = destinationLocation;
        this.departureTime = roomRequest.getDepartureTime();
        this.roomType = roomRequest.getRoomType();
    }
}
