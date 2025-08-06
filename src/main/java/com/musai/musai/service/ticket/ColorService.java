package com.musai.musai.service.ticket;

import com.musai.musai.dto.ticket.ColorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class ColorService {

    private static final String FAST_API_URL = "http://musai-ai:8000/color/recommend-color";
    private static final String LOCAL_API_URL = "http://localhost:8000/color/recommend-color";

    private String aiServerUrl = "http://musai-ai:8000/color/recommend-color";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ColorDTO getColorFromAiServer(MultipartFile image) throws Exception {

        // 이미지 파일 유효성 검사
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어있습니다.");
        }

        // 이미지 파일 바이트 준비
        ByteArrayResource byteResource = new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", byteResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                aiServerUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);

            if (responseMap.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                return parseColorData(data);
            } else {
                return parseColorData(responseMap);
            }

        } else {
            throw new RuntimeException("AI 서버 오류: " + response.getStatusCode());
        }
    }

    private ColorDTO parseColorData(Map<String, Object> data) {
        ColorDTO dto = new ColorDTO();

        if (data.containsKey("dominant_color")) {
            Map<String, Object> dominantColor = (Map<String, Object>) data.get("dominant_color");
            dto.setDominantRgb((java.util.List<Integer>) dominantColor.get("rgb"));

            java.util.List<Map<String, Object>> colorPalette =
                    (java.util.List<Map<String, Object>>) data.get("color_palette");

            java.util.List<java.util.List<Integer>> palette = new java.util.ArrayList<>();
            for (Map<String, Object> color : colorPalette) {
                palette.add((java.util.List<Integer>) color.get("rgb"));
            }
            dto.setPalette(palette);

        } else {
            dto.setDominantRgb((java.util.List<Integer>) data.get("dominant_rgb"));
            dto.setPalette((java.util.List<java.util.List<Integer>>) data.get("palette"));
        }

        return dto;
    }
}
