package com.musai.musai.service.preference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musai.musai.dto.arts.ArtDto;
import com.musai.musai.dto.preference.RecommendDTO;
import com.musai.musai.entity.arts.Art;
import com.musai.musai.entity.arts.Image;
import com.musai.musai.entity.preference.Recommend;
import com.musai.musai.repository.arts.ArtRepository;
import com.musai.musai.repository.arts.ImageRepository;
import com.musai.musai.repository.preference.RecommendRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendService2 {

    private final RecommendRepository preferenceRepository;
    private final ArtRepository artsRepository;
    private final ImageRepository imageRepository;
    private final ObjectMapper objectMapper;

    public RecommendService2(
            RecommendRepository preferenceRepository,
            ArtRepository artsRepository,
            ImageRepository imageRepository,
            ObjectMapper objectMapper
    ) {
        this.preferenceRepository = preferenceRepository;
        this.artsRepository = artsRepository;
        this.imageRepository = imageRepository;
        this.objectMapper = objectMapper;
    }

    public RecommendDTO getRecommendations(Long userId, int totalCount) throws Exception {
        Recommend preference = preferenceRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // JSON → Map 변환
        Map<String, Integer> styleScores = objectMapper.readValue(
                preference.getPreferences(),
                new TypeReference<Map<String, Integer>>() {}
        );

        // 점수 > 0 필터링
        Map<String, Integer> filtered = styleScores.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        int totalScore = filtered.values().stream().mapToInt(Integer::intValue).sum();
        if (totalScore == 0) {
            List<ArtDto> randomArts = artsRepository.findRandomAll(totalCount)
                    .stream().map(this::toDto).collect(Collectors.toList());
            return new RecommendDTO(userId, Collections.singletonMap("RANDOM", totalCount), randomArts);
        }

        // 비율 계산
        Map<String, Double> ratio = new HashMap<>();
        for (Map.Entry<String, Integer> entry : filtered.entrySet()) {
            ratio.put(entry.getKey(), entry.getValue() / (double) totalScore);
        }

        // 관심 작품 70~95%
        double interestTotal = 0.7 + (Math.random() * 0.25);
        int interestCount = (int) Math.round(totalCount * interestTotal);
        int randomCount = totalCount - interestCount;

        // 사조별 배분
        Map<String, Integer> perStyleCount = new HashMap<>();
        List<ArtDto> recommendations = new ArrayList<>();

        for (Map.Entry<String, Double> entry : ratio.entrySet()) {
            int count = (int) Math.round(entry.getValue() * interestCount);
            if (count > 0) {
                perStyleCount.put(entry.getKey(), count);

                // image에서 먼저 3~5개 가져오기
                int imageTarget = Math.min(count, 3 + (int) (Math.random() * 3));
                List<ArtDto> imageResults = imageRepository.findRandomByStyle(entry.getKey(), imageTarget)
                        .stream().map(this::toImageDtoAsArtDto).collect(Collectors.toList());

                // 부족분 계산
                int missing = count - imageResults.size();

                // art에서 부족분 채우기
                List<ArtDto> artResults = new ArrayList<>();
                if (missing > 0) {
                    artResults = artsRepository.findRandomByStyle(entry.getKey(), missing)
                            .stream().map(this::toDto).collect(Collectors.toList());
                }

                // 합치기
                recommendations.addAll(imageResults);
                recommendations.addAll(artResults);
            }
        }

        // RANDOM 처리
        if (randomCount > 0) {
            perStyleCount.put("RANDOM", randomCount);
            recommendations.addAll(
                    artsRepository.findRandomAll(randomCount)
                            .stream().map(this::toDto).collect(Collectors.toList())
            );
        }

        return new RecommendDTO(userId, perStyleCount, recommendations);
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

    // Image → ArtDto 로 맞춰주기 (프론트 일관성 위해)
    private ArtDto toImageDtoAsArtDto(Image image) {
        ArtDto dto = new ArtDto();
        dto.setPrimaryImageSmall(image.getPrimaryImageSmall());
        dto.setName(image.getName());
        dto.setTitle(image.getTitle());
        dto.setStyle(image.getStyle());
        dto.setDepartment("ImageTable"); // 출처 구분 가능
        return dto;
    }
}
