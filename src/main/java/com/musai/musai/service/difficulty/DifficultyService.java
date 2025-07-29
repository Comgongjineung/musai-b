package com.musai.musai.service.difficulty;

import com.musai.musai.dto.difficulty.DifficultyRequestDTO;
import com.musai.musai.dto.difficulty.DifficultyResponseDTO;
import com.musai.musai.entity.user.DefaultDifficulty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class DifficultyService {

    private final RestTemplate restTemplate = new RestTemplate();

    // AI 서버 기본 URL (예: http://localhost:8000)
    private String aiServerUrl = "http://musai-ai:8000";
    //private String aiServerUrl = "http://localhost:8000";

    public String convert(DefaultDifficulty level, String original) {
        // AI 서버 POST 경로 수정 (FastAPI 경로에 맞춤)
        String url = String.format("%s/difficulty/convert-difficulty", aiServerUrl);

        // form-data 만들기 (FastAPI가 Form으로 받음)
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("original", original);
        formData.add("level", level.name());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<DifficultyResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    DifficultyResponseDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getConvertedText();
            } else {
                // 실패시 원본 텍스트 반환
                return original;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return original;
        }
    }
}
