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
    // AI 서버 주소
    private static final String FAST_API_URL = "http://musai-ai:8000/web-detection/";
    private static final String LOCAL_API_URL = "http://localhost:8000/web-detection/";
    private final VuforiaService vuforiaService;

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
                FAST_API_URL,
                requestEntity,
                Map.class
        );

        ObjectMapper mapper = new ObjectMapper();
        RecogResponseDTO result = mapper.convertValue(response.getBody(), RecogResponseDTO.class);
        result.setLevel(level); // 선택한 난이도 저장
        return result;
    }

    // RecogService.java (혹은 새로운 Service에서 호출)
    public void analyzeAndRegisterToVuforia(MultipartFile file, String level, String bestGuess) throws Exception {
        RecogResponseDTO responseDTO = sendImageToAiServer(file, level, bestGuess);

        String imageUrl = responseDTO.getOriginal_image_url();
        // ✅ 여기에서 리사이즈+압축
        byte[] imageBytes = resizeAndCompressImage(new URL(imageUrl));

        String imageName = responseDTO.getVision_result();
        String metadata = new ObjectMapper().writeValueAsString(responseDTO.getGemini_result());

        vuforiaService.registerTarget(imageName, imageBytes, metadata);
    }

    public void registerImageToVuforia(RecogResponseDTO dto) throws Exception {
        String imageUrl = dto.getOriginal_image_url(); // Vuforia에 등록할 이미지 URL
        String imageName = dto.getVision_result();     // Target 이름
        String metadata = new ObjectMapper().writeValueAsString(dto.getGemini_result()); // Metadata

        byte[] imageBytes = resizeAndCompressImage(new URL(imageUrl)); // 리사이즈 + 압축

        vuforiaService.registerTarget(imageName, imageBytes, metadata);
    }

    // ✅ 이 부분 새로 추가
    private byte[] resizeAndCompressImage(URL imageUrl) throws IOException {
        BufferedImage originalImage = ImageIO.read(imageUrl);

        int targetWidth = 800;
        int targetHeight = 800;

        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        return baos.toByteArray();
    }

}