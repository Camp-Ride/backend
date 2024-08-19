package com.richjun.campride.room.service;

import static com.richjun.campride.global.exception.ExceptionCode.ALREADY_EXIST_USER;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_ROOM_ID;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_USER_ID;

import com.richjun.campride.chat.domain.repository.ChatMessageRedisTemplateRepository;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.global.location.domain.Location;
import com.richjun.campride.global.location.service.GeocodingService;
import com.richjun.campride.room.domain.Room;
import com.richjun.campride.room.domain.repository.RoomRepository;
import com.richjun.campride.room.request.RoomRequest;
import com.richjun.campride.room.response.RoomJoinedResponse;
import com.richjun.campride.room.response.RoomResponse;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ChatMessageRedisTemplateRepository chatMessageRedisTemplateRepository;

    @Transactional
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

    @Transactional(readOnly = true)
    public RoomResponse getRoom(final Long id) {

        final Room room = roomRepository.findById(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_ROOM_ID));

        return RoomResponse.from(room);
    }

    @Transactional
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

    @Transactional(readOnly = true)
    public Page<RoomResponse> searchRoomsPage(Pageable pageable) {

        return roomRepository.searchRoomsPage(pageable);
    }

    @Transactional(readOnly = true)
    public Page<RoomResponse> searchRoomsByDepartureAndDestinationPage(Pageable pageable, String departure,
                                                                       String destination) {

        return roomRepository.searchRoomsByDepartureAndDestinationPage(pageable, departure, destination);
    }

    @Transactional
    public RoomResponse joinRoom(Long id, CustomOAuth2User oAuth2User) {

        Room room = roomRepository.findById(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_ROOM_ID));

        if (room.isAleadyParticipant(oAuth2User.getName())) {
            throw new BadRequestException(ALREADY_EXIST_USER);
        }

        User user = userRepository.findBySocialLoginId(oAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        return RoomResponse.from(room.addParticipant(user));
    }

    @Transactional
    public RoomResponse exitRoom(Long id, CustomOAuth2User oAuth2User) {

        Room room = roomRepository.findById(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_ROOM_ID));

        if (room.isNotParticipant(oAuth2User.getName())) {
            throw new BadRequestException(NOT_FOUND_USER_ID);
        }

        User user = userRepository.findBySocialLoginId(oAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        return RoomResponse.from(room.removeParticipant(user));
    }

    public List<RoomJoinedResponse> getJoinedRooms(CustomOAuth2User oAuth2User) {

        User user = userRepository.findBySocialLoginId(oAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));
        List<Room> rooms = user.getRooms();

        return rooms.stream().map(room -> {
            return
                    RoomJoinedResponse.from(room,
                            chatMessageRedisTemplateRepository.getLatestMessageContent(room.getId()),
                            chatMessageRedisTemplateRepository.getUnreadMessageCount(room.getId(),
                                    room.getLastSeenMessageScore()));
        }).collect(Collectors.toList());

    }
}
