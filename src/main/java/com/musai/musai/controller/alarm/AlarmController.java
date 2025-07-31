package com.musai.musai.controller.alarm;

import com.musai.musai.service.alarm.AlarmService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
@SecurityRequirement(name = "bearerAuth")
public class AlarmController {

    private AlarmService alarmService;

    @PostMapping("/token")
    public ResponseEntity<String> saveFcmToken(
            @RequestParam Long userId,
            @RequestParam String token) {
        return ResponseEntity.ok(alarmService.saveToken(userId, token));
    }
}
