package com.richjun.campride.room.domain.repository;

import com.richjun.campride.room.domain.Room;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Boolean existsByIdAndLeaderNickname(Long roomId, String name);
}
