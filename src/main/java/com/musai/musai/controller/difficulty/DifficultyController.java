package com.musai.musai.controller.difficulty;

import com.musai.musai.dto.difficulty.DifficultyRequestDTO;
import com.musai.musai.dto.difficulty.DifficultyResponseDTO;
import com.musai.musai.entity.user.DefaultDifficulty;
import com.musai.musai.service.difficulty.DifficultyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/difficulty")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DifficultyController {

    private final DifficultyService difficultyService;

    @Operation(summary = "난이도별 해설 난이도 변경", description = "AI 서버와 연동하여 난이도별 해설을 반환합니다.")
    @PostMapping("/{level}")
    public ResponseEntity<DifficultyResponseDTO> convertDifficulty(
            @PathVariable DefaultDifficulty level,
            @RequestBody DifficultyRequestDTO request) {

        String convertedText = difficultyService.convert(level, request.getOriginal());
        return ResponseEntity.ok(new DifficultyResponseDTO(convertedText));
    }
}
