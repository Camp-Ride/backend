package com.richjun.campride.room.presentation;

import com.richjun.campride.room.request.RoomRequest;
import com.richjun.campride.room.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/room")
    public ResponseEntity<Long> createRoom(@RequestBody @Valid final RoomRequest roomRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.create(roomRequest));
    }


}
