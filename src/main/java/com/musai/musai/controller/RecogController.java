package com.musai.musai.controller;

import com.musai.musai.dto.RecogErrorDTO;
import com.musai.musai.dto.RecogResponseDTO;
import com.musai.musai.service.RecogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/recog")
@CrossOrigin(origins = "*") // Flutter와 연결 시 CORS 허용
public class RecogController {

    private final RecogService recogService;

    @Autowired
    public RecogController(RecogService recogService) {
        this.recogService = recogService;
    }

    @Operation(
            summary = "작품 인식",
            description = "이미지 파일을 업로드하여 AI 서버에 전달하고 분석 결과를 반환받습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "분석 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RecogResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "413",
                            description = "파일 업로드 용량 초과",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RecogErrorDTO.class),
                                    examples = @ExampleObject(
                                        name = "파일 업로드 용량 초과 예시",
                                        value = "{\"status\":413, \"message\":\"파일 업로드 용량을 초과했습니다.\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RecogErrorDTO.class)
                            )
                    )
            }
    )

    @PostMapping(value = "/analyzeAndRegister", consumes = "multipart/form-data")
    public ResponseEntity<?> analyzeAndRegisterImage(@RequestParam("file") MultipartFile file) {
        try {
            // 1. AI 분석 결과 먼저 받기
            RecogResponseDTO responseDTO = recogService.sendImageToAiServer(file);

            // 2. Vuforia 등록
            try {
                recogService.registerImageToVuforia(responseDTO);
                responseDTO.setVuforia_status("success");
            } catch (Exception ve) {
                responseDTO.setVuforia_status("fail: " + ve.getMessage());
            }

            // 3. JSON 응답: 분석 결과 + Vuforia 등록 여부 포함
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            RecogErrorDTO errorResponse = new RecogErrorDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    "AI 분석 실패 또는 Vuforia 처리 실패: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @PostMapping(value = "/analyze", consumes = "multipart/form-data")
    public ResponseEntity<?> analyzeImage(@RequestParam("file") MultipartFile file) {
        try {
            RecogResponseDTO response = recogService.sendImageToAiServer(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            RecogErrorDTO errorResponse = new RecogErrorDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    "AI 서버 분석 중 오류 발생: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/ping")
    public String healthCheck() {
        return "API 서버 살아있음";
    }
}
