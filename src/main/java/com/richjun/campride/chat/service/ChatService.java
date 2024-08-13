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


    public ChatMessage addMessage(ChatMessage message) {
        log.info(message.toString());
        return chatMessageRepository.save(message);
    }

    public void addMessage2(ChatMessage message) {
        log.info(message.toString());
        chatMessageRedisRepository.addMessage2(message);
    }


    public Page<ChatMessageResponse> getMessages(Long roomId, Pageable pageable) {

        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomId(roomId, pageable).stream()
                .collect(Collectors.toList());

        List<ChatMessageResponse> chatMessageResponses = chatMessages.stream()
                .map(chatMessage -> ChatMessageResponse.from(chatMessage.getId(), chatMessage))
                .collect(Collectors.toList());

        log.info(chatMessages.toString());
        log.info(chatMessageResponses.toString());

        return new PageImpl<>(chatMessageResponses, pageable,
                chatMessages.size());

    }

    public List<ChatMessageResponse> getMessages2(Long roomId) {

        List<ChatMessage> chatMessages = chatMessageRedisTemplateRepository.getMessages2(roomId, 0, 10);

        List<ChatMessageResponse> chatMessageResponses = chatMessages.stream()
                .map(chatMessage -> ChatMessageResponse.from(chatMessage.getId(), chatMessage))
                .sorted(Comparator.comparing(ChatMessageResponse::getId))
                .collect(Collectors.toList());

        log.info(chatMessages.toString());
        log.info(chatMessageResponses.toString());

        return chatMessageResponses;

    }

    public List<ChatMessageResponse> getLatest10Messages(Long roomId) {
//        List<ChatMessage> chatMessages = chatMessageRepository.findTopByRoomIdOrderByTimestampDesc(roomId);
//        log.info(chatMessages.toString());
//
//        long count = chatMessageRepository.countChatMessageByRoomIdContaining(roomId);
//
//        Pageable page = PageRequest.of(0, 1, Direction.DESC, "timestamp");
//
//        log.info(String.valueOf(count));
//
//        Page<ChatMessage> chatMessages = chatMessageRepository.findByRoomId(roomId, page);
//
//        log.info(chatMessages.toString());

        return null;

    }


    public ChatMessage findById(String id) {
        return chatMessageRepository.findById(id).orElse(null);
    }

//    public List<ChatMessageResponse> getMessages(String roomId, int startOffset, int count) {
//
//        List<MapRecord<String, String, Map<String, String>>> records = chatMessageRedisRepository.getMessages(
//                roomId, startOffset, count);
//
//        return records.stream()
//                .map(record -> {
//                    try {
//                        String messageJson = String.valueOf(record.getValue().get("message"));
//                        ChatMessage chatMessage = objectMapper.readValue(messageJson, ChatMessage.class);
//
//                        return ChatMessageResponse.from(String.valueOf(record.getId()), chatMessage); // 정적 팩토리 메서드 사용
//                    } catch (ClassCastException | JsonProcessingException e) {
//                        log.error("Error processing message: {}", record.getValue().get("message"), e);
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//    }


}