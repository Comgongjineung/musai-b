package com.musai.musai.dto.difficulty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DifficultyResponseDTO {

    @JsonProperty("converted")  // AI 서버 JSON 필드명과 매핑
    private String convertedText;
}
