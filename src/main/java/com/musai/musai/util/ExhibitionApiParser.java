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

        for (JsonNode node : root.get("data")) {
            ExhibitionDTO dto = new ExhibitionDTO();
            dto.setTitle(node.get("TITLE").asText());
            dto.setDescription(node.get("DESCRIPTION").asText());
            dto.setContributor(node.get("CONTRIBUTOR").asText());
            dto.setGenre(node.get("GENRE").asText());
            dto.setDuration(node.get("DURATION").asText());
            dto.setPeriod(node.get("PERIOD").asText());
            dto.setUrl(node.get("URL").asText());
            dto.setEvent_period(node.get("EVENT_PERIOD").asText());
            dto.setImage_object(node.get("IMAGE_OBJECT").asText());
            dto.setTable_of_contents(node.get("TABLE_OF_CONTENTS").asText());
            dto.setCntc_instt_nm(node.get("CNTC_INSTT_NM").asText());

            result.add(dto);
        }
        return result;
    }
}
