package com.richjun.campride.global.fcm.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.richjun.campride.global.fcm.dto.FCMMessageRequest;
import com.richjun.campride.global.fcm.dto.FCMSendRequest;
import com.richjun.campride.room.response.ParticipantResponse;
import com.richjun.campride.user.service.UserService;
import java.io.IOException;
import java.util.List;
import okhttp3.Response.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import org.springframework.http.HttpHeaders;

@Service
public class FCMService {

    private static final String API_URL = "https://fcm.googleapis.com/v1/projects/campride-87f0d/messages:send";


    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private String getUserFCMToken(Long userId) {
        return userService.getUserFCMToken(userId);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/campride-87f0d-firebase-adminsdk-mtnnn-a480ac194b.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private String makeMessage(String token, String title, String body)
            throws JsonProcessingException {
        FCMMessageRequest fcmMessage = FCMMessageRequest.builder()
                .message(FCMMessageRequest.Message.builder()
                        .token(token)
                        .notification(FCMMessageRequest.Notification.builder()
                                .title(title)
                                .body(body)
                                .build()
                        ).apns(FCMMessageRequest.Apns.builder().payload(FCMMessageRequest.Payload.builder()
                                .aps(FCMMessageRequest.Aps.builder().sound("default").build()).build()).build())
                        .build()).validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    public Response sendMessageTo(FCMSendRequest fcmSendRequest) {

        try {
            String message = makeMessage(getUserFCMToken(fcmSendRequest.getUserId()), fcmSendRequest.getTitle(),
                    fcmSendRequest.getBody());

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = RequestBody.create(message,
                    MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            Response response = client.newCall(request).execute();
            return response;

        } catch (IOException e) {
            return new Response.Builder().code(500).message("Internal Server Error").build();
        }
    }


}