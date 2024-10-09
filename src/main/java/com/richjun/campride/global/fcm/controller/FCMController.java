package com.richjun.campride.global.fcm.controller;

import com.richjun.campride.global.fcm.dto.FCMSendRequest;
import com.richjun.campride.global.fcm.service.FCMService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/v1/fcm")
public class FCMController {

    @Autowired
    private FCMService fcmService;

    @PostMapping("/send")
    public ResponseEntity<Response> pushMessage(@RequestBody @Validated FCMSendRequest fcmSendRequest)
            throws IOException {

        return ResponseEntity.ok().body(fcmService.sendMessageTo(fcmSendRequest));
    }
}