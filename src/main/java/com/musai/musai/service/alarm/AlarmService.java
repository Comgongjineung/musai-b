package com.musai.musai.service.alarm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.musai.musai.entity.user.Token;
import com.musai.musai.entity.user.User;
import com.musai.musai.repository.alarm.AlarmRepository;
import com.musai.musai.repository.alarm.TokenRepository;
import com.musai.musai.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ FCM sent successfully: " + response);
        } catch (Exception e) {
            System.out.println("❌ FCM 전송 실패: {}" + e.getMessage());
            throw new RuntimeException("FCM 전송 실패", e); // 예외 전파도 고려
        }
    }
}
