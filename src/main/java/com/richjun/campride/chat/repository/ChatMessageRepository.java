package com.richjun.campride.chat.repository;

import com.richjun.campride.chat.domain.ChatMessage;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, String> {
    Page<ChatMessage> findByRoomId(Long roomId, Pageable pageable);

    List<ChatMessage> findTopByRoomIdOrderByTimestampDesc(Long roomId);

}
