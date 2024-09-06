package com.richjun.campride.room.domain.repository.querydsl.impl;

import static com.richjun.campride.room.domain.QRoom.room;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.richjun.campride.room.domain.QRoom;
import com.richjun.campride.room.domain.Room;
import com.richjun.campride.room.domain.repository.querydsl.RoomRepositoryCustom;
import com.richjun.campride.room.response.RoomResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public Page<RoomResponse> searchRoomsPage(Pageable pageable) {

        List<Room> rooms = queryFactory.selectFrom(room)
                .orderBy(room.departureTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<RoomResponse> roomResponses = rooms.stream()
                .map(RoomResponse::from)
                .collect(Collectors.toList());

        long total = queryFactory.selectFrom(room)
                .fetchCount();

        return new PageImpl<>(roomResponses, pageable, total);
    }

    @Override
    public Page<RoomResponse> searchRoomsByDepartureAndDestinationPage(Pageable pageable, String departure,
                                                                       String destination) {

        List<Room> rooms = queryFactory.selectFrom(room)
                .where(departureContain(departure), destinationContain(destination))
                .orderBy(room.departureTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<RoomResponse> roomResponses = rooms.stream()
                .map(RoomResponse::from).toList();

        long total = queryFactory.selectFrom(room)
                .where(departureContain(departure), destinationContain(destination))
                .fetchCount();

        return new PageImpl<>(roomResponses, pageable, total);
    }


    private Predicate departureContain(String departure) {

        return departure == null ? null : room.departure.contains(departure);
    }

    private Predicate destinationContain(String destination) {

        return destination == null ? null : room.destination.contains(destination);
    }


}
