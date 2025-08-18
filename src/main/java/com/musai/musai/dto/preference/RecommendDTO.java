package com.musai.musai.dto.preference;

import com.musai.musai.dto.arts.ArtDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class RecommendDTO {
    private Long userId;
    private Map<String, Integer> styleCounts; // 사조별 몇 개 배정했는지
    private List<ArtDto> recommendations;     // 실제 추천 작품 리스트
}
