package com.musai.musai.dto.community;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이미지 업로드 응답")
public class ImageUploadResponseDTO {
    private String imageUrl;
    private String fileName;
    private String message;
    private boolean success;
}
