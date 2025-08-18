package com.musai.musai.controller.preference;

import com.musai.musai.dto.preference.PreferenceDTO;
import com.musai.musai.service.preference.PreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/preference")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "선호도", description = "사용자 선호도 관리 API")
public class PreferenceController {

    private final PreferenceService preferenceService;

    @PostMapping("/{userId}")
    @Operation(summary = "취향테스트 결과 반영", description = "취향테스트 결과를 바탕으로 사용자의 예술사조 선호도를 업데이트하고 업데이트된 선호도를 반환합니다.")
    public ResponseEntity<PreferenceDTO> updatePreferencesFromTest(
            @PathVariable Long userId,
            @RequestBody Map<String, Integer> testResults) {

        Map<String, Integer> updatedPreferences = preferenceService.updatePreferencesFromTestAndReturn(userId, testResults);
        
        PreferenceDTO response = PreferenceDTO.builder()
                .userId(userId)
                .preferences(updatedPreferences)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
