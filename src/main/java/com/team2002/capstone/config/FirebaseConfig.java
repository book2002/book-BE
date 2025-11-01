package com.team2002.capstone.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");

        try (InputStream serviceAccount = resource.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            if(FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("FirebaseApp initialized");
            }
        } catch (IOException e) {
            log.error("Firebase Admin SDK 초기화 실패: serviceAccountKey.json 파일을 찾을 수 없습니다.", e);
        }
    }
}


