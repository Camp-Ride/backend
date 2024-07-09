package com.richjun.campride.room.service;

import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_ROOM_ID;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_USER_ID;

import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.global.location.domain.Location;
import com.richjun.campride.global.location.service.GeocodingService;
import com.richjun.campride.room.domain.Room;
import com.richjun.campride.room.domain.repository.RoomRepository;
import com.richjun.campride.room.request.RoomRequest;
import com.richjun.campride.room.response.RoomResponse;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final GeocodingService geocodingService;

    public Long create(final RoomRequest roomRequest, final CustomOAuth2User oAuth2User) {

        Location departureLocation = geocodingService.getAddressCoordinates(roomRequest.getDeparture());
        Location destinationLocation = geocodingService.getAddressCoordinates(roomRequest.getDestination());

        User leaderUser = userRepository.findBySocialLoginId(oAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        List<User> participants = new ArrayList<>();
        participants.add(leaderUser);

        return roomRepository.save(
                        Room.of(roomRequest, participants, leaderUser.getNickname(), departureLocation, destinationLocation))
                .getId();
    }

    public RoomResponse getRoom(final Long id) {

        final Room room = roomRepository.findById(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_ROOM_ID));

        return RoomResponse.from(room);
    }

    public Long updateRoom(final Long id, final RoomRequest roomRequest) {

        Room room = roomRepository.findById(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_ROOM_ID));

        Location departureLocation = room.getDepartureLocation();
        Location destinationLocation = room.getDestinationLocation();

        if (!roomRequest.getDeparture().equals(room.getDeparture())) {
            departureLocation = geocodingService.getAddressCoordinates(roomRequest.getDeparture());
        }

        if (!roomRequest.getDestination().equals(room.getDestination())) {
            destinationLocation = geocodingService.getAddressCoordinates(roomRequest.getDestination());
        }

        room.update(roomRequest, departureLocation, destinationLocation);

        return room.getId();
    }
}
