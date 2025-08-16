package com.musai.musai.dto.bookmark;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "북마크 ")
public class BookmarkDTO {
    @Schema(description = "북마크 고유 아이디", example = "1")
    private Long bookmarkId;
    @Schema(description = "북마크 보유 사용자 아이디", example = "1")
    private Long userId;
    @Schema(description = "작품 제목", example = "모나리자 (La Gioconda)")
    private String title;
    @Schema(description = "작가 이름", example = "레오나르도 다 빈치 (Leonardo da Vinci)")
    private String artist;
    @Schema(description = "작품 해설", example = "르네상스 회화의 정점을 보여주는 대표적인 작품...")
    private String description;
    @Schema(description = "작품 이미지", example = "monarisa.jpg")
    private String imageUrl;
    @Schema(description = "작품 예술사조", example = "르네상스")
    private String style;
}
