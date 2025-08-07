package com.musai.musai.controller.alarm;

import com.musai.musai.dto.alarm.AlarmDTO;
import com.musai.musai.service.alarm.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "알림 목록 조회", description = "사용자의 모든 알림 목록을 조회합니다.")
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<AlarmDTO>> getAlarms(@PathVariable Long userId) {
        List<AlarmDTO> alarms = alarmService.getAlarmsByUserId(userId);
        return ResponseEntity.ok(alarms);
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    @PutMapping("/read/{alarmId}")
    public ResponseEntity<String> markAsRead(@PathVariable Long alarmId) {
        alarmService.markAsRead(alarmId);
        return ResponseEntity.ok("알림이 읽음 처리되었습니다.");
    }

    @Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림을 읽음 처리합니다.")
    @PutMapping("/readAll/{userId}")
    public ResponseEntity<String> markAllAsRead(@PathVariable Long userId) {
        alarmService.markAllAsRead(userId);
        return ResponseEntity.ok("모든 알림이 읽음 처리되었습니다.");
    }

    @Operation(summary = "읽지 않은 알림 개수 조회", description = "사용자의 읽지 않은 알림 개수를 조회합니다.")
    @GetMapping("/count/{userId}")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        Long count = alarmService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "알림 전체 삭제", description = "전체 알림을 삭제합니다. ")
    @DeleteMapping("/delete/all")
    public ResponseEntity<List<AlarmDTO>> deleteAllAlarms(@RequestParam Long userId) {
        List<AlarmDTO> alarms = alarmService.deleteAlarmByUserId(userId);
        return ResponseEntity.ok(alarms);
    }

    @Operation(summary = "특정 알림 삭제", description = "특정 알림을 삭제합니다.")
    @DeleteMapping("/delete/{alarmId}")
    public ResponseEntity<List<AlarmDTO>> deleteAlarmByAlarmId(@PathVariable Long alarmId) {
        List<AlarmDTO> alarms = alarmService.deleteAlarmByAlarmId(alarmId);
        return ResponseEntity.ok(alarms);
    }
}
