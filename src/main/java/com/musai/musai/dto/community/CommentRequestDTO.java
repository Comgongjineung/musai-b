package com.musai.musai.dto.community;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "댓글 요청")
public class CommentRequestDTO {
    @Schema(description = "사용자 고유 아이디", example = "4")
    private Long userId;

    @Schema(description = "게시물 고유 아이디", example = "1")
    private Long postId;

    @Schema(description = "답글 고유 아이디", example = "1")
    private Long parentCommentId;

    @Schema(description = "댓글 내용", example = "테스트용입니다.")
    private String content;

    @JsonCreator
    public static CommentRequestDTO create(
            @JsonProperty("userId") Long userId,
            @JsonProperty("postId") Long postId,
            @JsonProperty("parentCommentId") Long parentCommentId,
            @JsonProperty("content") String content) {
        return CommentRequestDTO.builder()
                .userId(userId)
                .postId(postId)
                .parentCommentId(parentCommentId)
                .content(content)
                .build();
    }
} 