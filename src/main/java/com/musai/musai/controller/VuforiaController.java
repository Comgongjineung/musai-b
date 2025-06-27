package com.musai.musai.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musai.musai.dto.VuforiaResponseDTO;
import com.musai.musai.service.VuforiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class VuforiaController {

    private final VuforiaService vuforiaService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Vuforia 이미지 등록", description = "이미지 파일을 multipart/form-data 형식으로 업로드하여 Vuforia 타겟을 등록합니다.")
    @PostMapping(value = "/vuforia/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VuforiaResponseDTO> registerTarget(
            @Parameter(description = "업로드할 이미지 파일", required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")))
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String imageName = "art_" + UUID.randomUUID();
            String metadata = "artworkdId=1234";
            byte[] imageBytes = file.getBytes();

            String result = vuforiaService.registerTarget(imageName, imageBytes, metadata);
            
            // JSON 응답에서 target_id 추출
            JsonNode jsonNode = objectMapper.readTree(result);
            String targetId = jsonNode.path("target_id").asText();
            
            return ResponseEntity.ok(VuforiaResponseDTO.success(targetId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(VuforiaResponseDTO.error(e.getMessage()));
        }
    }

    @Operation(summary = "Vuforia API 연결 테스트", description = "Vuforia API 키와 인증이 올바른지 테스트합니다.")
    @PostMapping("/vuforia/test")
    public ResponseEntity<String> testConnection() {
        try {
            String result = vuforiaService.testConnection();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("테스트 실패: " + e.getMessage());
        }
    }

    @Operation(summary = "Vuforia API 키 정보 확인", description = "설정된 API 키의 기본 정보를 확인합니다.")
    @GetMapping("/vuforia/keys")
    public ResponseEntity<String> checkKeys() {
        try {
            String result = vuforiaService.simpleTest();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("키 확인 실패: " + e.getMessage());
        }
    }
}