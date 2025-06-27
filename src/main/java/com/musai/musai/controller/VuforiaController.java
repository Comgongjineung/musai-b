package com.musai.musai.controller;

import com.musai.musai.service.VuforiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class VuforiaController {

    private final VuforiaService vuforiaService;

    @Operation(summary = "Vuforia 이미지 등록", description = "이미지 파일을 multipart/form-data 형식으로 업로드하여 Vuforia 타겟을 등록합니다.")
    @PostMapping(value = "/vuforia/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerTarget(
            @Parameter(description = "업로드할 이미지 파일", required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")))
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        String imageName = "art_" + UUID.randomUUID();
        String metadata = "artworkdId=1234";
        byte[] imageBytes = file.getBytes();

        String result = vuforiaService.registerTarget(imageName, imageBytes, metadata);
        return ResponseEntity.ok(result);
    }
}