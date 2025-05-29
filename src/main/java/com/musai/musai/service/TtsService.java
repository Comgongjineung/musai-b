package com.musai.musai.service;

import com.musai.musai.dto.TtsResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;
import java.util.Map;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;

@Service
public class TtsService {
    
    private static final String FAST_API_URL = "http://musai-ai:8000/tts/";
    private static final String LOCAL_API_URL = "http://localhost:8000/tts/";
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public byte[] synthesizeText(String text) {
        try {
            // URL 디코딩된 텍스트를 그대로 AI 서버로 전달
            String url = FAST_API_URL + "?text=" + text;

            // 바이너리로 받기
            ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                null,
                byte[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("AI 서버로부터 유효한 응답을 받지 못했습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("TTS 변환 중 오류 발생: " + e.getMessage());
        }
    }
    
    private float getMp3Duration(byte[] audioData) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bais);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            float durationInSeconds = (frames / format.getFrameRate());
            audioInputStream.close();
            return durationInSeconds;
        } catch (Exception e) {
            // duration 계산 실패 시 0 반환
            return 0;
        }
    }
}