package com.musai.musai.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        try {
            log.info("Firebase 초기화 시작...");
            
            InputStream serviceAccount = new ClassPathResource("firebase/firebase-service-account.json").getInputStream();
            log.info("Firebase 서비스 계정 파일 로드 완료");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("✅ Firebase 초기화 성공 - 프로젝트: {}", options.getProjectId());
            } else {
                log.info("Firebase가 이미 초기화되어 있습니다.");
            }
            
        } catch (IOException e) {
            log.error("❌ Firebase 초기화 실패 - 파일 읽기 오류: {}", e.getMessage(), e);
            throw new RuntimeException("Firebase 초기화 실패", e);
        } catch (Exception e) {
            log.error("❌ Firebase 초기화 실패 - 기타 오류: {}", e.getMessage(), e);
            throw new RuntimeException("Firebase 초기화 실패", e);
        }
    }
}