package com.richjun.campride.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {

        //src/main/resources/firebase/campride-87f0d-firebase-adminsdk-mtnnn-a480ac194b.json
        //app/firebase/campride-87f0d-firebase-adminsdk-mtnnn-a480ac194b.json

        FileInputStream serviceAccount = new FileInputStream(
                "/app/firebase/campride-87f0d-firebase-adminsdk-mtnnn-a480ac194b.json");

        FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(options);

        return FirebaseMessaging.getInstance(app);
    }

}
