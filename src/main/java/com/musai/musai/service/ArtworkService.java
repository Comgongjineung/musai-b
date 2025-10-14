package com.musai.musai.service;

import com.musai.musai.dto.ArtworkInfoDTO;
import com.musai.musai.util.MultipartInputStreamFileResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class ArtworkService {

    @Value("${fastapi.base-url}")
    private String fastapiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public ArtworkInfoDTO analyzeArtwork(MultipartFile file, String level, String bestGuess) throws IOException {
        String url = fastapiBaseUrl; // FastAPI endpoint

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        // 파일 전송
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

        // 문자열 필드는 HttpEntity로 감싸서 전송
        if (level != null && !level.isEmpty()) {
            HttpHeaders levelHeaders = new HttpHeaders();
            levelHeaders.setContentType(MediaType.TEXT_PLAIN);
            body.add("level", new HttpEntity<>(level, levelHeaders));
        }

        if (bestGuess != null && !bestGuess.isEmpty()) {
            HttpHeaders bestGuessHeaders = new HttpHeaders();
            bestGuessHeaders.setContentType(MediaType.TEXT_PLAIN);
            body.add("best_guess", new HttpEntity<>(bestGuess, bestGuessHeaders));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ArtworkInfoDTO> response = restTemplate.postForEntity(url, requestEntity, ArtworkInfoDTO.class);

        return response.getBody();
    }

}