package com.musai.musai.controller.arts;

import com.musai.musai.entity.arts.Met;
import com.musai.musai.service.arts.MetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MetController {

    private final MetService metService;

    public MetController(MetService metService) {
        this.metService = metService;
    }

    // 전체 메트 ObjectIDs 받아와서 DB에 저장 (수동 호출용)
    @GetMapping("/met/fetch")
    public String fetchAndSaveMetObjects() {
        metService.fetchAndSaveAllObjects();
        return "Met object IDs saved to DB";
    }

    // 저장된 모든 메트 엔티티 반환
    @GetMapping("/met/all")
    public List<Met> getAllMetObjects() {
        return metService.getAllMetEntities();
    }
}
