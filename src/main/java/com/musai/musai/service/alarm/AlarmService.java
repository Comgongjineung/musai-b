package com.musai.musai.service.alarm;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.musai.musai.entity.alarm.Alarm;
import com.musai.musai.entity.user.Token;
import com.musai.musai.entity.user.User;
import com.musai.musai.entity.user.Setting;
import com.musai.musai.repository.alarm.AlarmRepository;
import com.musai.musai.repository.alarm.TokenRepository;
import com.musai.musai.repository.user.UserRepository;
import com.musai.musai.repository.user.SettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.musai.musai.dto.alarm.AlarmDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final SettingRepository settingRepository;

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

    @Transactional
    public void sendCommentNotification(Long postAuthorId, String postTitle, String commentContent, Long commentCount) {
        try {
            Setting setting = settingRepository.findById(postAuthorId)
                    .orElse(Setting.builder().allowCalarm(true).build());
            
            if (!setting.getAllowCalarm()) {
                log.info("사용자 {}의 댓글 알림이 비활성화되어 있습니다.", postAuthorId);
                return;
            }

            Optional<Token> tokenOpt = tokenRepository.findByUserId(postAuthorId);
            if (tokenOpt.isEmpty()) {
                log.info("사용자 {}의 FCM 토큰이 없습니다.", postAuthorId);
                return;
            }

            String title = "새로운 댓글";
            String postTitleShort = postTitle.length() > 10 ? postTitle.substring(0, 10) + "..." : postTitle;
            String body = String.format("[%s] 게시글에 댓글(%d)이 달렸습니다", postTitleShort, commentCount);

            Alarm alarm = Alarm.builder()
                    .userId(postAuthorId)
                    .type("COMMENT")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            alarmRepository.save(alarm);

            sendFcm(tokenOpt.get().getToken(), title, body);
            log.info("댓글 알림 전송 완료 - 사용자: {}, 게시글: {}, 댓글 개수: {}", postAuthorId, postTitleShort, commentCount);

        } catch (Exception e) {
            log.error("댓글 알림 전송 실패 - 사용자: {}, 에러: {}", postAuthorId, e.getMessage(), e);
        }
    }

    @Transactional
    public void sendReplyNotification(Long commentAuthorId, String commentContent, int replyLevel) {
        try {
            Setting setting = settingRepository.findById(commentAuthorId)
                    .orElse(Setting.builder().allowRalarm(true).build());
            
            if (!setting.getAllowRalarm()) {
                log.info("사용자 {}의 답글 알림이 비활성화되어 있습니다.", commentAuthorId);
                return;
            }

            Optional<Token> tokenOpt = tokenRepository.findByUserId(commentAuthorId);
            if (tokenOpt.isEmpty()) {
                log.info("사용자 {}의 FCM 토큰이 없습니다.", commentAuthorId);
                return;
            }

            String title = "새로운 답글";
            String commentContentShort = commentContent.length() > 10 ? commentContent.substring(0, 10) + "..." : commentContent;
            String replyText = replyLevel == 1 ? "답글(1)" : "답글(" + replyLevel + ")";
            String body = String.format("[%s] 댓글에 %s이 달렸습니다", commentContentShort, replyText);

            Alarm alarm = Alarm.builder()
                    .userId(commentAuthorId)
                    .type("REPLY")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            alarmRepository.save(alarm);

            sendFcm(tokenOpt.get().getToken(), title, body);
            log.info("답글 알림 전송 완료 - 사용자: {}, 댓글: {}, 레벨: {}", commentAuthorId, commentContentShort, replyLevel);

        } catch (Exception e) {
            log.error("답글 알림 전송 실패 - 사용자: {}, 에러: {}", commentAuthorId, e.getMessage(), e);
        }
    }

    public List<AlarmDTO> getAlarmsByUserId(Long userId) {
        return alarmRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toAlarmDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        alarm.setIsRead(true);
        alarmRepository.save(alarm);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Alarm> unreadAlarms = alarmRepository.findByUserIdAndIsReadFalse(userId);
        for (Alarm alarm : unreadAlarms) {
            alarm.setIsRead(true);
        }
        alarmRepository.saveAll(unreadAlarms);
    }

    public Long getUnreadCount(Long userId) {
        return alarmRepository.countByUserIdAndIsReadFalse(userId);
    }

    private AlarmDTO toAlarmDTO(Alarm alarm) {
        return AlarmDTO.builder()
                .alarmId(alarm.getAlarmId())
                .userId(alarm.getUserId())
                .type(alarm.getType())
                .isRead(alarm.getIsRead())
                .createdAt(alarm.getCreatedAt())
                .build();
    }
}
