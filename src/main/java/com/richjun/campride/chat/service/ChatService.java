package com.richjun.campride.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richjun.campride.chat.domain.ChatMessage;
import com.richjun.campride.chat.domain.repository.ChatMessageRedisTemplateRepository;
import com.richjun.campride.chat.domain.repository.ChatMessageRepository;
import com.richjun.campride.chat.response.ChatMessageResponse;
import java.util.Comparator;
import java.util.List;
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

    private final ChatMessageRedisTemplateRepository chatMessageRedisRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final ChatMessageRedisTemplateRepository chatMessageRedisTemplateRepository;


    public void sendMessage(ChatMessage message) {
        simpMessagingTemplate.convertAndSend("/topic/messages/room/" + message.getRoomId(), message);
    }



    public void addMessage(ChatMessage message) {
        log.info(message.toString());
        chatMessageRedisRepository.addMessage2(message);
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
    }

    public void sendReaction(ChatMessage message) {
    }
}