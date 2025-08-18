package com.musai.musai.controller.tts;

import com.musai.musai.dto.tts.TtsResponseDTO;
import com.musai.musai.service.tts.TtsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tts")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class TtsController {

    private final TtsService ttsService;

    @Autowired
    public TtsController(TtsService ttsService) {
        this.ttsService = ttsService;
    }

    @Operation(
        summary = "텍스트를 음성으로 변환",
        description = "입력된 텍스트를 AI 서버를 통해 음성으로 변환합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "변환할 텍스트",
            required = true,
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(type = "string")
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "변환 성공",
                content = @Content(mediaType = "audio/mpeg")
            ),
            @ApiResponse(
                responseCode = "500",
                description = "서버 오류",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TtsResponseDTO.class)
                )
            )
        }
    )
    @PostMapping("/synthesize")
    public ResponseEntity<?> synthesizeText(@RequestBody String text) {
        try {
            byte[] audioData = ttsService.synthesizeText(text);
            if (audioData == null) {
                return ResponseEntity.internalServerError()
                    .body(new TtsResponseDTO(null, 0, "TTS 변환 중 오류가 발생했습니다."));
            }

            ByteArrayResource resource = new ByteArrayResource(audioData);
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tts.mp3")
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(resource);
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new TtsResponseDTO(null, 0, "TTS 변환 중 오류 발생: " + e.getMessage()));
        }
    }
} 