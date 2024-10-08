package com.richjun.campride.chat.presentation;

import com.richjun.campride.chat.domain.ChatMessage;
import com.richjun.campride.chat.domain.ChatMessageType;
import com.richjun.campride.chat.request.ChatJoinRequest;
import com.richjun.campride.chat.response.ChatMessageResponse;
import com.richjun.campride.chat.service.ChatService;
import com.richjun.campride.chat.service.KafkaProducerService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final KafkaProducerService kafkaProducerService;
    private final ChatService chatService;

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {

        // StompHeaderAccessor로 CONNECT_ACK 메시지 래핑
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        // CONNECT 메시지에서 헤더 접근
        GenericMessage<?> connectMessage = (GenericMessage<?>) accessor.getHeader("simpConnectMessage");

        if (connectMessage != null) {
            StompHeaderAccessor connectAccessor = StompHeaderAccessor.wrap(connectMessage);
            List<String> userIds = connectAccessor.getNativeHeader("userId");

            if (userIds != null && !userIds.isEmpty()) {
                String userId = userIds.get(0);
                log.info("Connected userId: " + userId);
                chatService.userConnected(userId);
            } else {
                log.info("UserId not found in CONNECT headers.");
            }
        } else {
            log.info("CONNECT message not found.");
        }

    }


    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String userId = sha.getSessionAttributes().get("userId").toString();
        chatService.userDisconnected(userId);
    }


    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        log.info(chatMessage.toString());
        kafkaProducerService.sendMessage(chatMessage);
    }

    @MessageMapping("/send/reaction")
    public void sendReaction(@Payload ChatMessage chatMessage) {
        log.info(chatMessage.toString());
        kafkaProducerService.sendReaction(chatMessage);
    }

    @MessageMapping("/send/leave")
    public void sendLeave(@Payload ChatMessage chatMessage) {
        log.info(chatMessage.toString());
        kafkaProducerService.sendLeave(chatMessage);
    }

    @MessageMapping("/send/kick")
    public void sendKick(@Payload ChatMessage chatMessage) {
        log.info(chatMessage.toString());
        kafkaProducerService.sendKick(chatMessage);
    }

    @PostMapping("/send/join/{roomId}")
    public ResponseEntity sendJoin(@PathVariable Long roomId,
                                   @Valid @RequestBody final ChatJoinRequest chatJoinRequest) {
        log.info(chatJoinRequest.toString());
        kafkaProducerService.sendJoin(
                new ChatMessage(null, roomId, chatJoinRequest.getUserId(), chatJoinRequest.getUserNickname(), "join",
                        "", LocalDateTime.now(), List.of(), "", ChatMessageType.JOIN, false));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/messages")
    public List<ChatMessageResponse> getMessages(@RequestParam Long roomId, @RequestParam int startOffset,
                                                 @RequestParam int count) {
        return chatService.getMessages(roomId, startOffset, count);
    }

    @GetMapping("/messages/latest")
    public List<ChatMessageResponse> getLatest10Messages(@RequestParam Long roomId) {
        return chatService.getLatest10Messages(roomId);
    }


    @PostMapping("/messages/stomp/test")
    public void testStomp() {
        log.info("test stomp");
        kafkaProducerService.sendMessage(
                new ChatMessage(1L, 1L, "test", "juntest", "test\ntest\ntest", "test", LocalDateTime.now(), List.of(),
                        "test",
                        ChatMessageType.TEXT, false));
    }


}