package com.musai.musai.dto.ar;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "작품 업데이트 요청")
public class ArtworkUpdateDTO {
    
    @Schema(description = "뷰포리아 target_id", example = "934847901755009590324", required = true)
    private String targetId;
    
    @Schema(description = "AR 포인트 목록", required = true)
    private List<PointDTO> points;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "포인트 정보")
    public static class PointDTO {
        
        @Schema(description = "X 좌표 (0~1 정규화)", example = "0.5", required = true)
        private BigDecimal x;
        
        @Schema(description = "Y 좌표 (0~1 정규화)", example = "0.3", required = true)
        private BigDecimal y;
        
        @Schema(description = "AR 해설", example = "핵심 포인트: 내용 요약", required = true)
        private String description;
    }
}
