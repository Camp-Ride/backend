package com.richjun.campride.chat.service;

import static com.richjun.campride.global.exception.ExceptionCode.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.chat.domain.ChatMessage;
import com.richjun.campride.chat.domain.repository.ChatMessageRedisTemplateRepository;
import com.richjun.campride.chat.domain.repository.ChatMessageRepository;
import com.richjun.campride.chat.response.ChatMessageResponse;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.global.exception.ExceptionCode;
import com.richjun.campride.global.fcm.dto.FCMSendRequest;
import com.richjun.campride.global.fcm.service.FCMService;
import com.richjun.campride.room.domain.Participant;
import com.richjun.campride.room.domain.Room;
import com.richjun.campride.room.domain.repository.ParticipantRepository;
import com.richjun.campride.room.domain.repository.RoomRepository;
import com.richjun.campride.room.response.ParticipantResponse;
import com.richjun.campride.room.response.RoomResponse;
import com.richjun.campride.room.service.RoomService;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Transactional
@Service
public class ChatService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageRedisTemplateRepository chatMessageRedisTemplateRepository;
    private final RoomService roomService;
    private final FCMService fcmService;

    private Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    public void sendFCMToOfflineUser(Long roomId, ChatMessage message) {
        RoomResponse room = roomService.getRoom(roomId);

        List<ParticipantResponse> offlineUsers = room.getParticipants().stream()
                .filter(participant -> !isUserOnline(participant.getId()))
                .collect(Collectors.toList());

        offlineUsers.forEach(offlineUser -> {
            fcmService.sendMessageTo(
                    new FCMSendRequest(offlineUser.getId(), message.getUserNickname(), message.getText()));
        });

    }


    public boolean isUserOnline(Long userId) {
        return onlineUsers.contains(userId);
    }

    public void sendMessage(ChatMessage message) {
        simpMessagingTemplate.convertAndSend("/topic/messages/room/" + message.getRoomId(), message);
    }


    public void addMessage(ChatMessage message) {
        log.info(message.toString());
        chatMessageRedisTemplateRepository.addMessage(message);
    }


    public List<ChatMessageResponse> getMessages(Long roomId, int startOffset, int count) {

        List<ChatMessage> chatMessages = chatMessageRedisTemplateRepository.getMessages(roomId, startOffset, count);

        List<ChatMessageResponse> chatMessageResponses = chatMessages.stream()
                .map(chatMessage -> ChatMessageResponse.from(chatMessage.getId(), chatMessage))
                .sorted(Comparator.comparing(ChatMessageResponse::getId))
                .collect(Collectors.toList());

        log.info(chatMessages.toString());
        log.info(chatMessageResponses.toString());

        return chatMessageResponses;

    }

    public List<ChatMessageResponse> getLatest10Messages(Long roomId) {

        List<ChatMessage> chatMessages = chatMessageRedisTemplateRepository.getMessages(roomId, 0, 10);

        List<ChatMessageResponse> chatMessageResponses = chatMessages.stream()
                .map(chatMessage -> ChatMessageResponse.from(chatMessage.getId(), chatMessage))
                .sorted(Comparator.comparing(ChatMessageResponse::getId))
                .collect(Collectors.toList());

        log.info(chatMessages.toString());
        log.info(chatMessageResponses.toString());

        return chatMessageResponses;

    }


    public void addReaction(ChatMessage message) {
        //레디스레포지토리로 해당 메세지 삭제
        chatMessageRedisTemplateRepository.deleteMessage(message);
        //레디스레포지토리로 해당 메세지 해당 아이디와 점수로 새롭게 추가
        chatMessageRedisTemplateRepository.updateReaction(message);
    }

    public void sendReaction(ChatMessage message) {
        simpMessagingTemplate.convertAndSend("/topic/messages/room/" + message.getRoomId(), message);
    }

    public void kickUser(ChatMessage message) {

    }

    public void userConnected(String userId) {
        onlineUsers.add(userId);
    }

    public void userDisconnected(String userId) {
        onlineUsers.remove(userId);
    }
}