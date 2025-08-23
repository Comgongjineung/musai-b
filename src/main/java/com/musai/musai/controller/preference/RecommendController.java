package com.musai.musai.controller.preference;

import com.musai.musai.dto.preference.RecommendDTO;
import com.musai.musai.service.preference.RecommendService;
import com.musai.musai.service.preference.RecommendService2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommend")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "선호도 기반 추천", description = "예술사조 선호도 기반 추천 API")
public class RecommendController {

    private final RecommendService recommendationService;
    private final RecommendService2 recommendationService2;

    public RecommendController(RecommendService recommendationService, RecommendService2 recommendationService2) {
        this.recommendationService = recommendationService;
        this.recommendationService2 = recommendationService2;
    }

    @Operation(summary = "선호도 기반 추천", description = "선호도 기반으로 추천합니다.")
    @GetMapping("/{userId}")
    public RecommendDTO recommend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int count
    ) throws Exception {
        return recommendationService.getRecommendations(userId, count);
    }

    @Operation(summary = "선호도 기반 추천", description = "선호도 기반으로 추천 + 더미데이터 포함")
    @GetMapping("/dummyData/{userId}")
    public RecommendDTO recommendDummy(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int count
    ) throws Exception {
        return recommendationService2.getRecommendations(userId, count);
    }
}

