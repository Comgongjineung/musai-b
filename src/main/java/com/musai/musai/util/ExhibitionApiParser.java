package com.musai.musai.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musai.musai.dto.exhibition.ExhibitionDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExhibitionApiParser {

    public List<ExhibitionDTO> fetchExhibitions() throws IOException {
        String apiUrl = "https://api.kcisa.kr/openapi/API_CCA_145/request";
        String response = new RestTemplate().getForObject(apiUrl, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        JsonNode items = root.path("body").path("items").path("item");

        List<ExhibitionDTO> result = new ArrayList<>();

        for (JsonNode node : items) {
            ExhibitionDTO dto = new ExhibitionDTO();

            dto.setTitle(getSafeText(node, "TITLE"));
            // 기간 관련 데이터가 JSON에 어떻게 있는지 정확히 확인 필요
            // 예시로 "START_DATE", "END_DATE" 키가 없으면 "PERIOD" 또는 "EVENT_PERIOD"로 파싱해야 함
//            dto.setPeriod(getSafeText(node, "PERIOD")); // 임시 저장
//            dto.setEvent_period(getSafeText(node, "EVENT_PERIOD")); // 임시 저장
//            dto.setEvent_site(getSafeText(node, "EVENT_SITE"));
//            dto.setContributor(getSafeText(node, "CONTRIBUTOR")); // realmName으로 활용 가능
//            dto.setImage_object(getSafeText(node, "IMAGE_OBJECT"));

            // GPS는 API에 없으면 null 처리
            dto.setGpsX(null);
            dto.setGpsY(null);

            result.add(dto);
        }
        return result;
    }

    private String getSafeText(JsonNode node, String key) {
        JsonNode value = node.get(key);
        return value != null ? value.asText() : null;
    }
}
