package com.musai.musai.dto.tts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "TTS 요청 DTO")
public class TtsRequestDTO {
    
    @Schema(description = "음성으로 변환할 텍스트", example = "이 작품은 레오나르도 다 빈치의 모나리자입니다.")
    private String text;
} 