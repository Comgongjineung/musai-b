package com.musai.musai.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musai.musai.dto.exhibition.ExhibitionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExhibitionApiParser {

    @Value("#{openapi.key}")
    private String apiKey;

    public List<ExhibitionDTO> fetchExhibitions() throws IOException {
        String apiUrl = "apis.data.go.kr/B553457/cultureinfo";
        String response = new RestTemplate().getForObject(apiUrl, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        JsonNode items = root.path("body").path("items").path("item");

        List<ExhibitionDTO> result = new ArrayList<>();


        return result;
    }
}
