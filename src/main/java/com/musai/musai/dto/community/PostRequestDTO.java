package com.musai.musai.dto.community;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 작성 요청값")
public class PostRequestDTO {
    private Long userId;
    private String title;
    private String content;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
}
