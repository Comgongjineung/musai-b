package com.musai.musai.controller.preference;

import com.musai.musai.dto.preference.RecommendDTO;
import com.musai.musai.service.preference.RecommendService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommend")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "선호도 기반 추천", description = "예술사조 선호도 기반 추천 API")
public class RecommendController {

    private final RecommendService recommendationService;

    public RecommendController(RecommendService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}")
    public RecommendDTO recommend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int count
    ) throws Exception {
        return recommendationService.getRecommendations(userId, count);
    }
}

