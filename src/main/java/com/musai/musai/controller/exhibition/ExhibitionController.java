package com.musai.musai.controller.exhibition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.musai.musai.entity.exhibition.Exhibition;
import com.musai.musai.service.exhibition.ExhibitionService;
import com.musai.musai.service.exhibition.ExhibitionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/exhibition")
@RequiredArgsConstructor
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

//    @GetMapping("/api/raw")
//    public String getRawApi(@RequestParam(defaultValue = "1") int pageNo,
//                            @RequestParam(defaultValue = "10") int numOfRows) throws Exception {
//        String rawJson = exhibitionApiService.getRawApiData(pageNo, numOfRows);
//        Object json = objectMapper.readValue(rawJson, Object.class);
//        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
//        return writer.writeValueAsString(json);
//    }


}