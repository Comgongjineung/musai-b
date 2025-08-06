package com.musai.musai.dto.community;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 수정 요청값")
public class PostUpdateDTO {
    private String title;
    private String content;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
}
