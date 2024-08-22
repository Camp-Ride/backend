package com.richjun.campride.room.domain.repository;

import com.richjun.campride.room.domain.Participant;
import com.richjun.campride.room.domain.Room;
import com.richjun.campride.user.domain.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<List<Participant>> findByUser(User user);

    Optional<Participant> findByUserAndRoom(User user, Room room);
}
