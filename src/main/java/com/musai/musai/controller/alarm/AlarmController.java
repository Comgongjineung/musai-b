package com.musai.musai.controller.alarm;

import com.musai.musai.service.alarm.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/alarm")
@SecurityRequirement(name = "bearerAuth")
public class AlarmController {

    private final AlarmService alarmService;

    @Operation(summary = "FCM 토큰 발급", description = "FCM 토큰을 발급합니다.")
    @PostMapping("/token")
    public ResponseEntity<String> saveFcmToken(
            @RequestParam Long userId,
            @RequestParam String token) {
        return ResponseEntity.ok(alarmService.saveToken(userId, token));
    }

    @Operation(summary = "테스트 알림", description = "테스트로 실제 기기에 알림을 보냅니다.")
    @PostMapping("/test")
    public ResponseEntity<String> sendFcmMessage(
            @RequestParam String targetToken,
            @RequestParam String title,
            @RequestParam String body) {
        try {
            log.info("FCM 테스트 요청 - 토큰: {}, 제목: {}, 내용: {}", targetToken, title, body);
            
            // 토큰 유효성 검사
            if (targetToken == null || targetToken.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("FCM 토큰이 비어있습니다.");
            }
            
            alarmService.sendFcm(targetToken, title, body);
            return ResponseEntity.ok("FCM 전송 성공");
        } catch (Exception e) {
            log.error("FCM 테스트 실패: {}", e.getMessage(), e);
            
            if (e.getMessage().contains("Requested entity was not found")) {
                return ResponseEntity.status(400).body("유효하지 않은 FCM 토큰입니다. 실제 모바일 앱에서 생성된 토큰을 사용하세요.");
            } else if (e.getMessage().contains("Permission") || e.getMessage().contains("denied")) {
                return ResponseEntity.status(500).body("Firebase 권한 오류: " + e.getMessage());
            } else {
                return ResponseEntity.status(500).body("FCM 전송 실패: " + e.getMessage());
            }
        }
    }
}
