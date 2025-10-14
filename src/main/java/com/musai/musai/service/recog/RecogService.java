package com.musai.musai.service.recog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musai.musai.dto.recog.RecogResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class RecogService {

    private static final String FAST_API_URL = "http://musai-ai:8000/web-detection/";
    private static final String LOCAL_API_URL = "http://localhost:8000/web-detection/";

    public RecogResponseDTO sendImageToAiServer(MultipartFile file, String level, String bestGuess) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        ByteArrayResource imageAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", imageAsResource);
        body.add("best_guess", bestGuess != null ? bestGuess : "");
        body.add("level", level != null ? level : "중");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                LOCAL_API_URL,
                requestEntity,
                Map.class
        );

        ObjectMapper mapper = new ObjectMapper();
        RecogResponseDTO result = mapper.convertValue(response.getBody(), RecogResponseDTO.class);
        result.setLevel(level); // 선택한 난이도 저장
        return result;
    }
}