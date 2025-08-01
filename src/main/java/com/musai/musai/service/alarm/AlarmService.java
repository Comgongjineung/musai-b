package com.musai.musai.service.alarm;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.musai.musai.entity.user.Token;
import com.musai.musai.entity.user.User;
import com.musai.musai.repository.alarm.AlarmRepository;
import com.musai.musai.repository.alarm.TokenRepository;
import com.musai.musai.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Transactional
    public String saveToken(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        Token userToken = tokenRepository.findByUserId(userId)
                .orElse(Token.builder().userId(userId).build());

        userToken.setToken(token);
        tokenRepository.save(userToken);

        return "FCM 토큰 저장 완료";
    }

    public void sendFcm(String targetToken, String title, String body) {
        try {
            log.info("FCM 전송 시작 - 토큰: {}, 제목: {}, 내용: {}", targetToken, title, body);
            
            // Firebase 앱이 초기화되었는지 확인
            if (FirebaseApp.getApps().isEmpty()) {
                throw new RuntimeException("Firebase가 초기화되지 않았습니다.");
            }
            
            log.info("Firebase 앱 상태 확인 완료 - 앱 개수: {}", FirebaseApp.getApps().size());
            
            Message message = Message.builder()
                    .setToken(targetToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM 전송 성공: {}", response);
            
        } catch (Exception e) {
            log.error("❌ FCM 전송 실패 - 상세 에러: {}", e.getMessage(), e);
            
            // 권한 관련 에러인지 확인
            if (e.getMessage().contains("Permission") || e.getMessage().contains("denied")) {
                String errorMessage = "Firebase 권한 오류: " + e.getMessage() + 
                    "\n\n해결 방법:" +
                    "\n1. Google Cloud Console에서 프로젝트 'musai-459505' 선택" +
                    "\n2. IAM 및 관리 → IAM 메뉴로 이동" +
                    "\n3. 서비스 계정 'musai-396@musai-459505.iam.gserviceaccount.com' 편집" +
                    "\n4. 다음 역할 추가: Firebase Admin, Cloud Messaging Admin, Service Account Token Creator" +
                    "\n5. Firebase Console에서 Cloud Messaging 설정 확인";
                throw new RuntimeException(errorMessage, e);
            } else if (e.getMessage().contains("InvalidRegistration") || e.getMessage().contains("NotRegistered")) {
                throw new RuntimeException("유효하지 않은 FCM 토큰: " + e.getMessage(), e);
            } else {
                throw new RuntimeException("FCM 전송 실패: " + e.getMessage(), e);
            }
        }
    }
}
