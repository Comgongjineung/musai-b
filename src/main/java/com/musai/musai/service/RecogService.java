package com.musai.musai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musai.musai.dto.RecogRequestDTO;
import com.musai.musai.dto.RecogResponseDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class RecogService {
    // FastAPI 서버 주소
    private static final String FAST_API_URL = "http://musai-ai:8000/web-detection/";
    private static final String LOCAL_API_URL = "http://localhost:8000/web-detection/";


    public RecogResponseDTO sendImageToAiServer(MultipartFile file) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        // 이미지 파일을 ByteArrayResource로 변환
        ByteArrayResource imageAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        // multipart/form-data 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", imageAsResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // FastAPI 서버로 POST 요청
        ResponseEntity<Map> response = restTemplate.postForEntity(
                LOCAL_API_URL,
                requestEntity,
                Map.class
        );

        ObjectMapper mapper = new ObjectMapper();
        RecogResponseDTO result = mapper.convertValue(response.getBody(), RecogResponseDTO.class);

        return result;
    }
}