package com.musai.musai.dto.preference;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 선호도 응답 DTO")
public class PreferenceDTO {
    
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    
    @Schema(description = "예술사조별 선호도 점수", example = "{\"인상주의\": 3, \"후기 인상주의\": 2, \"야수파 & 표현주의\": 1}")
    private Map<String, Integer> preferences;
}
