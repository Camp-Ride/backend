package com.richjun.campride.room.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.richjun.campride.room.response.RoomResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class RoomServiceTest {


    @Autowired
    private RoomService roomService;


    @DisplayName("주소검색 - 출발지와 같은 방만을 반환한다.")
    @Test
    void searchRoomsByDepartureAndDestinationPage_DepartureIsPresentAndDestinationIsNull_RoomSearchedOnlyDeparture() {

        //given
        String departure = "경기 성남시 분당구 판교역로 166";
        String destination = null;
        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<RoomResponse> rooms = roomService.searchRoomsByDepartureAndDestinationPage(pageable, departure,
                destination);

        //then
        assertThat(rooms).isNotNull();
        assertThat(rooms.getContent()).isNotEmpty();
        assertThat(rooms.getContent()).allMatch(room -> room.getDeparture().equals(departure));
    }


    @DisplayName("주소검색 - 도착지와 같은 방만을 반환한다.")
    @Test
    void searchRoomsByDepartureAndDestinationPage_DepartureIsNullAndDestinationIsPresent_RoomSearchedOnlyDestination() {
        String departure = null;
        String destination = "경기 성남시 분당구 대왕판교로 477";
    }


    @DisplayName("주소검색 - 도착지와 출발지가 같은 방만을 반환한다.")
    @Test
    void searchRoomsByDepartureAndDestinationPage_DepartureIsPresentAndDestinationIsPresent_RoomSearchedDepartureAndDestination() {
        String departure = "경기 성남시 분당구 판교역로 166";
        String destination = "경기 성남시 분당구 대왕판교로 477";
    }


    @DisplayName("주소검색(페이징) - 1페이지 5개의 Room을 반환한다. ")
    @Test
    void searchRoomsByDepartureAndDestinationPage_PagingWithPageable_ReturnPage1With5Room() {

    }


}
