package com.musai.musai.controller.arts;

import com.musai.musai.service.arts.ArtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/arts")
public class ArtController {

    private final ArtService artService;

    public ArtController(ArtService artService) {
        this.artService = artService;
    }

    @GetMapping("/fetch")
    public ResponseEntity<String> fetchAndSaveArtworks(
            @RequestParam int startId,
            @RequestParam int endId,
            @RequestParam(defaultValue = "5") int batchSize,
            @RequestParam(defaultValue = "10000") int pauseMillis) {

        artService.fetchAndSaveArtworksFromMetIds((long) startId, (long) endId, batchSize, pauseMillis);
        return ResponseEntity.ok("Fetch and save process started.");
    }
}
