package com.musai.musai.service.arts;

import com.fasterxml.jackson.databind.JsonNode;
import com.musai.musai.entity.arts.Art;
import com.musai.musai.repository.arts.ArtRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class ArtService {

    private final ArtRepository artRepository;

    public ArtService(ArtRepository artRepository) {
        this.artRepository = artRepository;
    }

    // WebClient 생성
    private WebClient createNewWebClient() {
        return WebClient.builder()
                .baseUrl("https://collectionapi.metmuseum.org")
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .defaultHeader("Connection", "close")
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                                .build()
                )
                .build();
    }

    // objectEndDate 기준 시기 매핑
    private String determineStyle(Integer endDate, String culture) {
        if (endDate == null) return null;

        // 아시아 지역 구분
        if (!isEmpty(culture)) {
            switch (culture) {
                case "China":
                case "Japan":
                case "Korea":
                case "Mongolia":
                case "Tibet":
                case "Taiwan":
                    return "동아시아";
                case "Thailand":
                case "Vietnam":
                case "Cambodia":
                case "Laos":
                case "Myanmar":
                case "Indonesia":
                case "Philippines":
                case "Malaysia":
                case "Singapore":
                    return "동남아시아";
                case "India":
                case "Pakistan":
                case "Bangladesh":
                case "Sri Lanka":
                case "Nepal":
                case "Bhutan":
                case "Maldives":
                    return "남아시아";
                case "Kazakhstan":
                case "Uzbekistan":
                case "Turkmenistan":
                case "Kyrgyzstan":
                case "Tajikistan":
                    return "중앙아시아";
                case "Iran":
                case "Persia":  // MET API에 따라 둘 다
                case "Iraq":
                case "Syria":
                case "Turkey":
                case "Anatolia":
                case "Saudi Arabia":
                case "Afghanistan":
                case "Armenia":
                case "Georgia":
                    return "서아시아/중동";
            }
        }

        // 서양 미술 기준
        if (endDate <= 400) return "고대 미술";
        else if (endDate <= 1400) return "중세 미술";
        else if (endDate <= 1600) return "르네상스";
        else if (endDate <= 1750) return "바로크";
        else if (endDate <= 1780) return "로코코";
        else if (endDate <= 1830) return "신고전주의";
        else if (endDate <= 1850) return "낭만주의";
        else if (endDate <= 1880) return "사실주의";
        else if (endDate <= 1890) return "인상주의";
        else if (endDate <= 1905) return "후기 인상주의";
        else if (endDate <= 1910) return "아르누보";
        else if (endDate <= 1930) return "표현주의";
        else if (endDate <= 1920) return "입체주의";
        else if (endDate <= 1945) return "초현실주의";
        else if (endDate <= 1960) return "추상표현주의";
        else if (endDate <= 1970) return "팝아트";
        else return "현대미술";
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }


    // JSON → Art 엔티티 변환 (필수 필드 체크)
    private Art convertJsonToArt(JsonNode json) {
        String primaryImageSmall = json.hasNonNull("primaryImageSmall") ? json.get("primaryImageSmall").asText() : null;
        String name = json.hasNonNull("artistDisplayName") ? json.get("artistDisplayName").asText() : null;
        String department = json.hasNonNull("department") ? json.get("department").asText() : null;
        String title = json.hasNonNull("title") ? json.get("title").asText() : null;
        String culture = json.hasNonNull("culture") ? json.get("culture").asText() : null;
        String period = json.hasNonNull("period") ? json.get("period").asText() : null;
        String objectDate = json.hasNonNull("objectDate") ? json.get("objectDate").asText() : null;
        Integer objectBeginDate = json.hasNonNull("objectBeginDate") ? json.get("objectBeginDate").asInt() : null;
        Integer objectEndDate = json.hasNonNull("objectEndDate") ? json.get("objectEndDate").asInt() : null;
        Integer objectID = json.hasNonNull("objectID") ? json.get("objectID").asInt() : null;
        String classification = json.hasNonNull("classification") ? json.get("classification").asText() : null;
        String objectName = json.hasNonNull("objectName") ? json.get("objectName").asText() : null;
        // 필수 필드 체크
        if (isEmpty(primaryImageSmall) || isEmpty(department) || isEmpty(title)
                || isEmpty(objectDate) || objectEndDate == null || objectID == null) {
            return null;
        }


        // title 중복 체크
        if (artRepository.existsByTitle(title)) {
            return null;
        }

        Art art = new Art();
        art.setPrimaryImageSmall(primaryImageSmall);
        art.setName(name);
        art.setDepartment(department);
        art.setTitle(title);
        art.setCulture(culture);
        art.setPeriod(period);
        art.setObjectDate(objectDate);
        art.setObjectBeginDate(objectBeginDate);
        art.setObjectEndDate(objectEndDate);
        art.setObjectID(objectID);
        art.setClassification(classification);
        art.setStyle(determineStyle(objectEndDate, culture));
        art.setObjectName(objectName);
        return art;
    }


    // 단일 작품 가져오기
    public void fetchAndSaveArtwork(Integer objectID) {
        WebClient webClient = createNewWebClient();

        try {
            JsonNode json = webClient.get()
                    .uri("/public/collection/v1/objects/{id}", objectID)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (json != null) {
                Art art = convertJsonToArt(json);
                if (art != null) {
                    artRepository.save(art);
                    System.out.printf("✅ Saved artwork ID %d%n", objectID);
                } else {
                    System.out.printf("⚠️ Skipped artwork ID %d due to missing required fields or duplicate title%n", objectID);
                }
            }
        } catch (WebClientResponseException e) {
            System.err.printf(
                    "❌ HTTP %d for objectID %d%n--- Headers --- %s%n--- Body --- %s%n",
                    e.getRawStatusCode(),
                    objectID,
                    e.getHeaders(),
                    e.getResponseBodyAsString()
            );
        } catch (Exception e) {
            System.err.printf("💥 Unexpected error for objectID %d - %s%n", objectID, e.toString());
            e.printStackTrace();
        }
    }

    // 범위별 작품 가져오기 (배치 단위 + 대기 시간)
    public void fetchAndSaveArtworksByRangeWithPause(int startId, int endId, int batchSize, int pauseMillis) {
        for (int i = startId; i <= endId; i += batchSize) {
            int batchStart = i;
            int batchEnd = Math.min(i + batchSize - 1, endId);
            System.out.printf("📦 Processing batch from %d to %d%n", batchStart, batchEnd);

            for (int id = batchStart; id <= batchEnd; id++) {
                fetchAndSaveArtwork(id);
            }

            if (batchEnd < endId) {
                System.out.printf("⏸ Sleeping %d milliseconds before next batch...%n", pauseMillis);
                try {
                    Thread.sleep(pauseMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
