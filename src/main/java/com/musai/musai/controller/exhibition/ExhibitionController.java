package com.musai.musai.controller.exhibition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.musai.musai.entity.exhibition.Exhibition;
import com.musai.musai.service.exhibition.ExhibitionService;
import com.musai.musai.service.exhibition.ExhibitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/exhibition")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ExhibitionController {
    private final ExhibitionService exhibitionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 전체 전시회 조회
    @Operation(summary = "전시회 목록 전체 조회", description = "전시회 목록을 조회합니다.")
    @GetMapping
    public List<Exhibition> getAllExhibitions() {
        return exhibitionService.getAllExhibitions();
    }

    // 특정 전시회 상세 조회
    @GetMapping("/{id}")
    public Exhibition getExhibitionById(@PathVariable Long id) {
        return exhibitionService.getExhibitionById(id);
    }

}