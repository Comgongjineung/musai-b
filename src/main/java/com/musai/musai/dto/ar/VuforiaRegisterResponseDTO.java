package com.musai.musai.dto.ar;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "뷰포리아 등록 응답")
public class VuforiaRegisterResponseDTO {
    
    @Schema(description = "응답 성공 여부", example = "true")
    private boolean success;
    
    @Schema(description = "뷰포리아 target_id", example = "934847901755009590324")
    private String targetId;
    
    @Schema(description = "작품 제목", example = "모나리자")
    private String title;
    
    @Schema(description = "응답 메시지", example = "작품이 성공적으로 등록되었습니다.")
    private String message;
    
    public static VuforiaRegisterResponseDTO success(String targetId, String title) {
        return new VuforiaRegisterResponseDTO(true, targetId, title, "작품이 성공적으로 등록되었습니다.");
    }
    
    public static VuforiaRegisterResponseDTO error(String message) {
        return new VuforiaRegisterResponseDTO(false, null, null, message);
    }
}
