package com.musai.musai.dto.ar;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "AR 응답값")
public class ARResponseDTO {
    
    @Schema(description = "뷰포리아 target_id", example = "934847901755009590324")
    private String target_id;
    
    @Schema(description = "AR 포인트 목록")
    private List<ARPoint> points;
    
    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(description = "AR 포인트 정보")
    public static class ARPoint {
        
        @Schema(description = "포인트 ID", example = "pt1")
        private String id;
        
        @Schema(description = "X 좌표 (0~1 정규화)", example = "0.5")
        private Double x;
        
        @Schema(description = "Y 좌표 (0~1 정규화)", example = "0.3")
        private Double y;
        
        @Schema(description = "AR 해설", example = "**대상명**: 내용 요약")
        private String description;
    }
}
