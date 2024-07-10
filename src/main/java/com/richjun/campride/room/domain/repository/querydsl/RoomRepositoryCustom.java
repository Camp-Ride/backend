package com.richjun.campride.room.domain.repository.querydsl;

import com.richjun.campride.room.domain.Room;
import com.richjun.campride.room.response.RoomResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomRepositoryCustom {

    Page<RoomResponse> searchRoomsPage(Pageable pageable);

}
