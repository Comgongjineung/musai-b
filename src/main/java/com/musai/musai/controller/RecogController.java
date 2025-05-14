package com.musai.musai.controller;

import com.musai.musai.service.RecogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
            summary = "이미지 업로드 및 AI 분석 요청",
            description = "이미지 파일을 업로드하여 AI 서버에 전달하고 분석 결과를 반환받습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "분석 성공"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PostMapping(value = "/analyze", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> analyzeImage(
            @Parameter(description = "이미지 파일", required = true, content = @Content(mediaType = "application/octet-stream"))
            @RequestParam("file") MultipartFile file) throws Exception {

        Map<String, Object> aiResponse = recogService.sendImageToAiServer(file);
        return ResponseEntity.ok(aiResponse);
    }

    @GetMapping("/ping")
    public String healthCheck() {
        return "API 서버 살아있음";
    }
}
