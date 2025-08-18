package com.musai.musai.controller.ar;

import com.musai.musai.dto.ar.ARResponseDTO;
import com.musai.musai.dto.ar.ArtworkUpdateDTO;
import com.musai.musai.dto.ar.VuforiaRegisterResponseDTO;
import com.musai.musai.entity.ar.Point;
import com.musai.musai.service.ar.ARService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ar")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "AR 해설", description = "AR 해설 관련 API")
public class ARController {

    private final ARService arService;

    @Operation(summary = "뷰포리아 이미지 등록", description = "이미지를 뷰포리아에 등록하고 target_id를 발급받아 DB에 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "작품 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VuforiaRegisterResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VuforiaRegisterResponseDTO.class)
                    )
            )
    })
    @PostMapping(value = "/vuforia/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VuforiaRegisterResponseDTO> registerArtwork(
            @Parameter(description = "등록할 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "작품 제목", required = true)
            @RequestParam("title") String title) {
        
        try {
            log.info("작품 등록 요청: title={}, filename={}", title, file.getOriginalFilename());
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(VuforiaRegisterResponseDTO.error("이미지 파일이 비어있습니다."));
            }

            String targetId = arService.registerArtwork(file, title);
            
            return ResponseEntity.ok(VuforiaRegisterResponseDTO.success(targetId, title));
            
        } catch (Exception e) {
            log.error("작품 등록 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(VuforiaRegisterResponseDTO.error("작품 등록 실패: " + e.getMessage()));
        }
    }

    @Operation(summary = "AI 서버에서 메타데이터 받아오기", description = "AI 서버에 이미지를 전송하여 좌표와 해설을 받아와서 DB에 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "AI 서버에서 메타데이터 받아오기 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = java.util.List.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (target_id를 찾을 수 없음)"
            )
    })
    @PostMapping(value = "/points/ai-update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Point>> updateArtworkMetadataFromAI(
            @Parameter(description = "뷰포리아 target_id", required = true)
            @RequestParam("target_id") String targetId,
            @Parameter(description = "AI 분석용 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file) {
        
        try {
            log.info("AI 서버에서 메타데이터 받아오기 요청: target_id={}, filename={}", targetId, file.getOriginalFilename());
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(List.of());
            }
            
            List<com.musai.musai.entity.ar.Point> savedPoints = arService.updateArtworkMetadataFromAI(targetId, file);
            
            return ResponseEntity.ok(savedPoints);
            
        } catch (Exception e) {
            log.error("AI 서버에서 메타데이터 받아오기 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    @Operation(summary = "AR 데이터 조회", description = "target_id로 좌표와 설명을 조회하여 유니티에 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "AR 데이터 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ARResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (target_id를 찾을 수 없음)"
            )
    })
    @GetMapping
    public ResponseEntity<ARResponseDTO> getARData(
            @Parameter(description = "뷰포리아 target_id", required = true)
            @RequestParam("target_id") String targetId) {
        
        try {
            log.info("AR 데이터 조회 요청: target_id={}", targetId);
            
            ARResponseDTO arData = arService.getARDataByTargetId(targetId);
            
            return ResponseEntity.ok(arData);
            
        } catch (Exception e) {
            log.error("AR 데이터 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
