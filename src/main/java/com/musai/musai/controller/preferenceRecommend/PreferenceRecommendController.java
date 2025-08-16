package com.musai.musai.controller.preferenceRecommend;

import com.musai.musai.dto.preferenceRecommend.PreferenceRecommendDTO;
import com.musai.musai.service.preferenceRecommend.PreferenceRecommendService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommend")
public class PreferenceRecommendController {

    private final PreferenceRecommendService recommendationService;

    public PreferenceRecommendController(PreferenceRecommendService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}")
    public PreferenceRecommendDTO recommend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int count
    ) throws Exception {
        return recommendationService.getRecommendations(userId, count);
    }
}

