package com.richjun.campride.global.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.richjun.campride.global.fcm.dto.FCMSendRequest;
import com.richjun.campride.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @Autowired
    private UserService userService;

    public String sendFCMNotification(FCMSendRequest fcmSendRequest) {

        Message fcmMessage = Message.builder()
                .putData("message", fcmSendRequest.getBody())
                .putData("title", fcmSendRequest.getTitle())
                .setToken(getUserFCMToken(fcmSendRequest.getUserId()))
                .build();

        try {
            return FirebaseMessaging.getInstance().send(fcmMessage);
        } catch (FirebaseMessagingException e) {
            return "FCM send failed: " + e.getMessage();
        }

    }

    private String getUserFCMToken(Long userId) {

        return userService.getUserFCMToken(userId);
    }
}