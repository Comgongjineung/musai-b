package com.musai.musai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "AI 이미지 분석 응답")
@AllArgsConstructor
@NoArgsConstructor
public class RecogResponseDTO {

    @Schema(description = "비전 인식 결과 (작품 이름)", example = "Mona Lisa")
    private String vision_result;

    @Schema(description = "Gemini AI로부터 받은 상세 설명 결과")
    private GeminiResult gemini_result;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Gemini AI의 예술 작품 설명")
    public static class GeminiResult {

        @Schema(description = "작품 이미지", example = "이미지 링크")
        private String image_url;

        @Schema(description = "작품 제목", example = "모나리자 (La Gioconda)")
        private String title;

        @Schema(description = "작가", example = "레오나르도 다 빈치 (Leonardo da Vinci)")
        private String artist;

        @Schema(description = "작품 제작 시기", example = "1503년 - 1517년경")
        private String year;

        @Schema(description = "예술 양식", example = "르네상스")
        private String style;

        @Schema(description = "작품 설명", example = "르네상스 회화의 정점을 보여주는 대표적인 작품...")
        private String description;

        @Schema(description = "에러 메시지", example = "서버 연결 오류")
        private String error;
    }

    @Schema(description = "이미지 링크", example = "이미지 링크")
    private String original_image_url;
}
