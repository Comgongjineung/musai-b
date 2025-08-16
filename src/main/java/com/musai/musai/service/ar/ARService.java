package com.musai.musai.service.ar;

import com.musai.musai.dto.ar.ARResponseDTO;
import com.musai.musai.dto.ar.ArtworkUpdateDTO;
import com.musai.musai.service.recog.VuforiaService;
import com.musai.musai.entity.ar.ArArt;
import com.musai.musai.entity.ar.Point;
import com.musai.musai.repository.ar.ArArtRepository;
import com.musai.musai.repository.ar.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ARService {

    private final VuforiaService vuforiaService;
    private final ArArtRepository arArtRepository;
    private final PointRepository pointRepository;
    private final RestTemplate restTemplate;

    private static final String AI_SERVER_AR_URL = "http://musai-ai:8000/ar/points-gemini";
    private static final String LOCAL_SERVER_AR_URL = "http://localhost:8000/ar/points-gemini";

    public String registerArtwork(MultipartFile image, String title) throws Exception {
        log.info("작품 등록 시작: title={}, filename={}", title, image.getOriginalFilename());

        String vuforiaResponse = vuforiaService.registerTarget(title, image.getBytes(), null);
        String targetId = extractTargetIdFromResponse(vuforiaResponse);
        
        if (targetId == null || targetId.isEmpty()) {
            throw new RuntimeException("뷰포리아에서 target_id를 받지 못했습니다.");
        }

        ArArt arArt = ArArt.builder()
                .title(title)
                .targetId(targetId)
                .build();
        arArtRepository.save(arArt);
        
        log.info("작품 등록 완료: title={}, target_id={}", title, targetId);
        return targetId;
    }

    @Transactional
    public List<Point> updateArtworkMetadataFromAI(String targetId, MultipartFile image) throws Exception {
        log.info("AI 서버에서 메타데이터 받아오기 시작: target_id={}", targetId);

        ArArt arArt = arArtRepository.findByTargetId(targetId)
                .orElseThrow(() -> new RuntimeException("해당 target_id를 가진 작품을 찾을 수 없습니다: " + targetId));

        List<ARResponseDTO.ARPoint> aiPoints = getARDescriptionFromAIServer(image);
        
        if (aiPoints != null && !aiPoints.isEmpty()) {
            pointRepository.deleteByArArt_ArtId(arArt.getArtId());

            List<Point> points = aiPoints.stream()
                    .map(aiPoint -> Point.builder()
                            .arArt(arArt)
                            .x(java.math.BigDecimal.valueOf(aiPoint.getX()))
                            .y(java.math.BigDecimal.valueOf(aiPoint.getY()))
                            .description(aiPoint.getDescription())
                            .build())
                    .collect(Collectors.toList());
            
            pointRepository.saveAll(points);
            log.info("AI 서버에서 받은 포인트 저장 완료: points_count={}", points.size());
            return points;
        } else {
            log.warn("AI 서버에서 포인트를 받지 못했습니다.");
            return List.of();
        }
    }

    public ARResponseDTO getARDataByTargetId(String targetId) {
        log.info("AR 데이터 조회 시작: target_id={}", targetId);

        ArArt arArt = arArtRepository.findByTargetId(targetId)
                .orElseThrow(() -> new RuntimeException("해당 target_id를 가진 작품을 찾을 수 없습니다: " + targetId));

        List<Point> points = pointRepository.findByArArt_ArtId(arArt.getArtId());

        ARResponseDTO responseDTO = new ARResponseDTO();
        responseDTO.setTarget_id(targetId);
        
        List<ARResponseDTO.ARPoint> arPoints = points.stream()
                .map(this::convertToARPoint)
                .collect(Collectors.toList());
        
        responseDTO.setPoints(arPoints);
        
        log.info("AR 데이터 조회 완료: target_id={}, points_count={}", targetId, arPoints.size());
        return responseDTO;
    }

    private String extractTargetIdFromResponse(String vuforiaResponse) {
        try {
            if (vuforiaResponse.contains("\"target_id\":")) {
                int startIndex = vuforiaResponse.indexOf("\"target_id\":") + 13;
                int endIndex = vuforiaResponse.indexOf("\"", startIndex);
                if (endIndex > startIndex) {
                    return vuforiaResponse.substring(startIndex, endIndex);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("뷰포리아 응답에서 target_id 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    private ARResponseDTO.ARPoint convertToARPoint(Point point) {
        ARResponseDTO.ARPoint arPoint = new ARResponseDTO.ARPoint();
        arPoint.setId("pt" + point.getId());
        arPoint.setX(point.getX().doubleValue());
        arPoint.setY(point.getY().doubleValue());
        arPoint.setDescription(point.getDescription());
        return arPoint;
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

            String aiServerUrl = AI_SERVER_AR_URL;

            ResponseEntity<List> response = restTemplate.postForEntity(
                    aiServerUrl,
                    requestEntity,
                    List.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Object> pointsList = response.getBody();
                log.info("AI 서버에서 AR 정보 수신 성공: points_count={}", pointsList.size());

                List<ARResponseDTO.ARPoint> points = pointsList.stream()
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


}
