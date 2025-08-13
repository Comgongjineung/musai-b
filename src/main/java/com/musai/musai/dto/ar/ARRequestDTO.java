package com.musai.musai.dto.ar;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "AR 요청값")
public class ARRequestDTO {
    
    @Schema(description = "작품 제목", example = "모나리자", required = true)
    private String title;
}
