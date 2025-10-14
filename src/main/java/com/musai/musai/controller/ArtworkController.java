package com.musai.musai.controller;

import com.musai.musai.dto.ArtworkInfoDTO;
import com.musai.musai.service.ArtworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/artwork")
public class ArtworkController {

    @Autowired
    private ArtworkService artworkService;

    @PostMapping(value = "/analyze", consumes = "multipart/form-data")
    public ResponseEntity<ArtworkInfoDTO> analyze(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "level", required = false) String level,
            @RequestParam(name = "best_guess", required = false) String bestGuess) {
        try {
            ArtworkInfoDTO responseDTO = artworkService.analyzeArtwork(file, level, bestGuess);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            // RecogErrorDTO 대신 HTTP 500 오류 응답 반환
            return ResponseEntity.internalServerError().build();
        }
    }
}