package com.richjun.campride.room.presentation;

import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.room.response.RoomJoinedResponse;
import com.richjun.campride.room.response.RoomResponse;
import com.richjun.campride.room.request.RoomRequest;
import com.richjun.campride.room.service.RoomService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/room")
    public ResponseEntity<Long> createRoom(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                           @RequestBody @Valid final RoomRequest roomRequest) {

        return ResponseEntity.ok().body(roomService.create(roomRequest, oAuth2User));
    }

    @GetMapping("/room/{id}")
    public ResponseEntity<RoomResponse> getRoom(@PathVariable Long id) {
        return ResponseEntity.ok().body(roomService.getRoom(id));
    }

    @PutMapping("/room/{id}")
    @PreAuthorize("@roomPermissionService.isCreatedBy(#id, #oAuth2User)")
    public ResponseEntity<Long> updateRoom(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @PathVariable Long id,
                                           @RequestBody @Valid final RoomRequest roomRequest) {

        return ResponseEntity.ok().body(roomService.updateRoom(id, roomRequest));
    }


    @GetMapping("/room")
    public ResponseEntity<Page<RoomResponse>> searchRoomsPage(Pageable pageable) {
        return ResponseEntity.ok().body(roomService.searchRoomsPage(pageable));
    }


    @GetMapping("/room/joined")
    public ResponseEntity<List<RoomJoinedResponse>> getJoinedRooms(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok().body(roomService.getJoinedRooms(oAuth2User));
    }

    @GetMapping("/room/address")
    public ResponseEntity<Page<RoomResponse>> searchRoomsByDepartureAndDestinationPage(Pageable pageable,
                                                                                       @RequestParam(required = false) String departure,
                                                                                       @RequestParam(required = false) String destination) {
        return ResponseEntity.ok()
                .body(roomService.searchRoomsByDepartureAndDestinationPage(pageable, departure, destination));
    }

    @PutMapping("/room/{id}/join")
    public ResponseEntity<RoomResponse> joinRoom(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                                 @PathVariable Long id) {

        return ResponseEntity.ok().body(roomService.joinRoom(id, oAuth2User));
    }

    @PutMapping("/room/{id}/exit")
    public ResponseEntity<RoomResponse> exitRoom(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                                 @PathVariable Long id) {

        return ResponseEntity.ok().body(roomService.exitRoom(id, oAuth2User));
    }

    @PutMapping("/room/{id}/last-message")
    public ResponseEntity<RoomResponse> updateLastMessage(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @PathVariable Long id) {
        return ResponseEntity.ok().body(roomService.updateLastMessage(id,oAuth2User));
    }









}
