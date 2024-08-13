package com.richjun.campride.room.domain.repository;

import com.richjun.campride.room.domain.Room;
import com.richjun.campride.room.domain.repository.querydsl.RoomRepositoryCustom;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom {
    Boolean existsByIdAndLeaderNickname(Long roomId, String name);

}
