package com.musai.musai.dto.community;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "댓글 수정 요청")
public class CommentUpdateDTO {
    @Schema(description = "댓글 내용", example = "수정된 댓글 내용입니다.")
    private String content;

    @JsonCreator
    public static CommentUpdateDTO create(
            @JsonProperty("content") String content) {
        return CommentUpdateDTO.builder()
                .content(content)
                .build();
    }
} 