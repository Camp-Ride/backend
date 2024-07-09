package com.richjun.campride.room.service;

import com.richjun.campride.global.location.domain.Location;
import com.richjun.campride.global.location.service.GeocodingService;
import com.richjun.campride.room.domain.Room;
import com.richjun.campride.room.domain.repository.RoomRepository;
import com.richjun.campride.room.request.RoomRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final GeocodingService geocodingService;

    public Long create(final RoomRequest roomRequest) {

        Location departureLocation = geocodingService.getAddressCoordinates(roomRequest.getDeparture());
        Location destinationLocation = geocodingService.getAddressCoordinates(roomRequest.getDestination());

        return roomRepository.save(
                Room.of(roomRequest, departureLocation, destinationLocation)).getId();

    }

}
