package com.musai.musai.service.preferenceRecommend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musai.musai.dto.arts.ArtDto;
import com.musai.musai.dto.preferenceRecommend.PreferenceRecommendDTO;
import com.musai.musai.entity.arts.Art;
import com.musai.musai.entity.preferenceRecommend.PreferenceRecommend;
import com.musai.musai.repository.arts.ArtRepository;
import com.musai.musai.repository.preferenceRecommend.PreferenceRecommendRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PreferenceRecommendService {

    private final PreferenceRecommendRepository preferenceRepository;
    private final ArtRepository artsRepository;
    private final ObjectMapper objectMapper;

    public PreferenceRecommendService(
            PreferenceRecommendRepository preferenceRepository,
            ArtRepository artsRepository,
            ObjectMapper objectMapper
    ) {
        this.preferenceRepository = preferenceRepository;
        this.artsRepository = artsRepository;
        this.objectMapper = objectMapper;
    }

    public PreferenceRecommendDTO getRecommendations(Long userId, int totalCount) throws Exception {
        PreferenceRecommend preference = preferenceRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // JSON → Map 변환
        Map<String, Integer> styleScores = objectMapper.readValue(
                preference.getPreferences(),
                new TypeReference<Map<String, Integer>>() {}
        );

        // 점수 > 0 필터링
        Map<String, Integer> filtered = new HashMap<>();
        for (Map.Entry<String, Integer> entry : styleScores.entrySet()) {
            if (entry.getValue() > 0) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }

        int totalScore = filtered.values().stream().mapToInt(Integer::intValue).sum();
        if (totalScore == 0) {
            List<ArtDto> randomArts = artsRepository.findRandomAll(totalCount)
                    .stream().map(this::toDto).collect(Collectors.toList());
            return new PreferenceRecommendDTO(userId, Collections.singletonMap("RANDOM", totalCount), randomArts);
        }

        // 비율 계산
        Map<String, Double> ratio = new HashMap<>();
        for (Map.Entry<String, Integer> entry : filtered.entrySet()) {
            ratio.put(entry.getKey(), entry.getValue() / (double) totalScore);
        }

        // 관심 사조 총합 제한 (70% ~ 95%)
        double interestTotal = Math.max(0.7, Math.min(1.0, 0.95));
        int interestCount = (int) Math.round(totalCount * interestTotal);
        int randomCount = totalCount - interestCount;

        // 사조별 배분 → 실제 작품 조회
        Map<String, Integer> perStyleCount = new HashMap<>();
        List<ArtDto> recommendations = new ArrayList<>();

        for (Map.Entry<String, Double> entry : ratio.entrySet()) {
            int count = (int) Math.round(entry.getValue() * interestCount);
            if (count > 0) {
                perStyleCount.put(entry.getKey(), count);
                recommendations.addAll(
                        artsRepository.findRandomByStyle(entry.getKey(), count)
                                .stream().map(this::toDto).collect(Collectors.toList())
                );
            }
        }

        // RANDOM 남은 거 처리
        if (randomCount > 0) {
            perStyleCount.put("RANDOM", randomCount);
            recommendations.addAll(
                    artsRepository.findRandomAll(randomCount)
                            .stream().map(this::toDto).collect(Collectors.toList())
            );
        }

        return new PreferenceRecommendDTO(userId, perStyleCount, recommendations);
    }

    // Entity → DTO 변환
    private ArtDto toDto(Art art) {
        ArtDto dto = new ArtDto();
        dto.setPrimaryImageSmall(art.getPrimaryImageSmall());
        dto.setName(art.getName());
        dto.setDepartment(art.getDepartment());
        dto.setTitle(art.getTitle());
        dto.setCulture(art.getCulture());
        dto.setPeriod(art.getPeriod());
        dto.setObjectDate(art.getObjectDate());
        dto.setObjectBeginDate(art.getObjectBeginDate());
        dto.setObjectEndDate(art.getObjectEndDate());
        dto.setObjectID(art.getObjectID());
        dto.setClassification(art.getClassification());
        dto.setStyle(art.getStyle());
        dto.setObjectName(art.getObjectName());
        return dto;
    }
}
