package com.musai.musai.controller.ar;

import com.musai.musai.dto.ar.ARResponseDTO;
import com.musai.musai.service.ar.ARService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ar")
@SecurityRequirement(name = "bearerAuth")
public class ARController {

    private final ARService arService;

    @Operation(summary = "AR 해설", description = "이미지 파일을 받아서 AI로 분석하여 AR 해설, 좌표값, target_id를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "AR 해설 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ARResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (이미지 파일 없음)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "415",
                    description = "지원하지 않는 이미지 형식",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ARResponseDTO> getARDescription(
            @Parameter(description = "분석할 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "작품 제목", required = true)
            @RequestParam(value = "title", required = true) String title) {
        
        try {
            log.info("AR 해설 요청 수신: filename={}, size={}, title={}", 
                    file.getOriginalFilename(), file.getSize(), title);
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                return ResponseEntity.status(415).build();
            }
            
            ARResponseDTO responseDTO = arService.getARDescription(file, title);
            return ResponseEntity.ok(responseDTO);
            
        } catch (Exception e) {
            log.error("AR 해설 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
