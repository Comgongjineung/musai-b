package com.musai.musai.dto.tts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "TTS 응답 DTO")
@AllArgsConstructor
@NoArgsConstructor
public class TtsResponseDTO {
    
    @Schema(description = "음성 파일의 Base64 인코딩된 문자열")
    private String audioContent;
    
    @Schema(description = "음성 파일 길이(초)")
    private double duration;
    
    @Schema(description = "오류 메시지 (발생 시)")
    private String error;
} 