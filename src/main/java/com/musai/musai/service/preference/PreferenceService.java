package com.musai.musai.service.preference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musai.musai.entity.preference.Preference;
import com.musai.musai.repository.preference.PreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void increaseStyleScore(Long userId, String style, int increment) {
        Preference preference = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> initializeUserPreferences(userId));
        
        Map<String, Integer> preferences = convertJsonToMap(preference.getPreferences());

        int currentScore = preferences.getOrDefault(style, 0);
        int newScore = Math.max(0, currentScore + increment);
        preferences.put(style, newScore);

        preference.setPreferences(convertMapToJson(preferences));
        preferenceRepository.save(preference);
        
        log.info("사용자 {}의 {} 예술사조 점수가 {}에서 {}로 변경되었습니다.", 
                userId, style, currentScore, newScore);
    }

    // 취향테스트 결과로 선호도 업데이트
    @Transactional
    public void updatePreferencesFromTest(Long userId, Map<String, Integer> testResults) {
        Preference preference = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> initializeUserPreferences(userId));
        
        Map<String, Integer> currentPreferences = convertJsonToMap(preference.getPreferences());

        testResults.forEach((style, score) -> {
            int currentScore = currentPreferences.getOrDefault(style, 0);
            int newScore = Math.max(0, currentScore + score);
            currentPreferences.put(style, newScore);
        });
        
        preference.setPreferences(convertMapToJson(currentPreferences));
        preferenceRepository.save(preference);
        
        log.info("사용자 {}의 취향테스트 결과가 반영되었습니다.", userId);
    }

    // 취향테스트 결과로 선호도 업데이트 후 업데이트된 선호도 반환
    @Transactional
    public Map<String, Integer> updatePreferencesFromTestAndReturn(Long userId, Map<String, Integer> testResults) {
        Preference preference = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> initializeUserPreferences(userId));
        
        Map<String, Integer> currentPreferences = convertJsonToMap(preference.getPreferences());

        testResults.forEach((style, score) -> {
            int currentScore = currentPreferences.getOrDefault(style, 0);
            int newScore = Math.max(0, currentScore + score);
            currentPreferences.put(style, newScore);
        });
        
        preference.setPreferences(convertMapToJson(currentPreferences));
        preferenceRepository.save(preference);
        
        log.info("사용자 {}의 취향테스트 결과가 반영되었습니다.", userId);
        
        // 업데이트된 선호도 반환
        return currentPreferences;
    }

    @Transactional
    public Preference initializeUserPreferences(Long userId) {
        Map<String, Integer> defaultPreferences = createDefaultPreferences();
        
        Preference preference = Preference.builder()
                .userId(userId)
                .preferences(convertMapToJson(defaultPreferences))
                .build();
        
        return preferenceRepository.save(preference);
    }

    private Map<String, Integer> createDefaultPreferences() {
        Map<String, Integer> preferences = new HashMap<>();
        preferences.put("고대 미술", 0);
        preferences.put("중세 미술", 0);
        preferences.put("르네상스", 0);
        preferences.put("바로크", 0);
        preferences.put("로코코", 0);
        preferences.put("신고전주의", 0);
        preferences.put("낭만주의", 0);
        preferences.put("사실주의", 0);
        preferences.put("인상주의", 0);
        preferences.put("후기 인상주의", 0);
        preferences.put("아르누보", 0);
        preferences.put("야수파 & 표현주의", 0);
        preferences.put("입체주의", 0);
        preferences.put("미래주의 & 구성주의", 0);
        preferences.put("다다 & 초현실주의", 0);
        preferences.put("추상표현주의", 0);
        preferences.put("팝아트", 0);
        preferences.put("미니멀리즘 & 현대미술", 0);
        preferences.put("동아시아", 0);
        preferences.put("동남아시아", 0);
        preferences.put("남아시아", 0);
        preferences.put("중앙아시아", 0);
        preferences.put("서아시아 / 중동", 0);
        
        return preferences;
    }

    private String convertMapToJson(Map<String, Integer> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Map을 JSON으로 변환하는 중 오류 발생", e);
            return "{}";
        }
    }

    private Map<String, Integer> convertJsonToMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Integer>>() {});
        } catch (JsonProcessingException e) {
            log.error("JSON을 Map으로 변환하는 중 오류 발생", e);
            return createDefaultPreferences();
        }
    }
}
