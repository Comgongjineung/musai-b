package com.musai.musai.service.ar;

import com.musai.musai.dto.ar.ARResponseDTO;
import com.musai.musai.service.recog.VuforiaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ARService {

    private final VuforiaService vuforiaService;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String AI_SERVER_AR_URL = "http://musai-ai:8000/ar/points-gemini";
    private static final String LOCAL_SERVER_AR_URL = "http://localhost:8000/ar/points-gemini";

    public ARResponseDTO getARDescription(MultipartFile file, String title) throws Exception {
        log.info("AR 해설 요청: filename={}, size={}, title={}", 
                file.getOriginalFilename(), file.getSize(), title);

		List<ARResponseDTO.ARPoint> aiPoints = getARDescriptionFromAIServer(file);

		String targetId;
		try {
			String metadata = title;
			targetId = vuforiaService.ensureTargetByTitle(title, file.getBytes(), metadata);
			log.info("뷰포리아 타겟 확보: title={}, target_id={}", title, targetId);
		} catch (Exception e) {
			log.warn("뷰포리아 타겟 확보 실패: {}", e.getMessage(), e);
			targetId = "temp_" + System.currentTimeMillis();
		}

        ARResponseDTO responseDTO = new ARResponseDTO();
        responseDTO.setTarget_id(targetId);
        responseDTO.setPoints(aiPoints);
        
        log.info("AR 해설 응답 완성: target_id={}, points_count={}", 
                responseDTO.getTarget_id(), 
                responseDTO.getPoints() != null ? responseDTO.getPoints().size() : 0);
        
        return responseDTO;
    }

    private List<ARResponseDTO.ARPoint> getARDescriptionFromAIServer(MultipartFile file) throws Exception {
        try {
            ByteArrayResource imageAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", imageAsResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            //매번 바꿔야 됨
            String aiServerUrl = AI_SERVER_AR_URL;

            ResponseEntity<List> response = restTemplate.postForEntity(
                    aiServerUrl,
                    requestEntity,
                    List.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("AI 서버에서 AR 정보 수신 성공: points_count={}", response.getBody().size());

                List<ARResponseDTO.ARPoint> points = response.getBody().stream()
                        .map(this::convertMapToARPoint)
                        .toList();
                
                return points;
            } else {
                throw new RuntimeException("AI 서버 응답 오류: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("AI 서버 AR API 호출 실패: {}", e.getMessage(), e);

            log.warn("AI 서버 연결 실패로 임시 데이터 반환");

            ARResponseDTO.ARPoint tempPoint = new ARResponseDTO.ARPoint();
            tempPoint.setId("pt1");
            tempPoint.setX(0.5);
            tempPoint.setY(0.5);
            tempPoint.setDescription("작품: AI 서버 연결 실패로 인한 임시 해설입니다. 파일: " + file.getOriginalFilename());
            
            return List.of(tempPoint);
        }
    }

    @SuppressWarnings("unchecked")
    private ARResponseDTO.ARPoint convertMapToARPoint(Object obj) {
        if (obj instanceof java.util.Map) {
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
            
            ARResponseDTO.ARPoint point = new ARResponseDTO.ARPoint();
            point.setId((String) map.get("id"));

            Object xObj = map.get("x");
            if (xObj instanceof Number) {
                point.setX(((Number) xObj).doubleValue());
            } else if (xObj instanceof String) {
                point.setX(Double.parseDouble((String) xObj));
            }
            
            Object yObj = map.get("y");
            if (yObj instanceof Number) {
                point.setY(((Number) yObj).doubleValue());
            } else if (yObj instanceof String) {
                point.setY(Double.parseDouble((String) yObj));
            }
            
            point.setDescription((String) map.get("description"));
            return point;
        }

        ARResponseDTO.ARPoint defaultPoint = new ARResponseDTO.ARPoint();
        defaultPoint.setId("pt1");
        defaultPoint.setX(0.5);
        defaultPoint.setY(0.5);
        defaultPoint.setDescription("작품: 응답 변환 실패");
        return defaultPoint;
    }
}
