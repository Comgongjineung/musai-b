package com.musai.musai.dto.community;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글")
public class CommentDTO {
    @Schema(description = "댓글 고유 아이디", example = "1")
    private Long commentId;

    @Schema(description = "사용자 고유 아이디", example = "4")
    private Long userId;

    @Schema(description = "게시물 고유 아이디", example = "1")
    private Long postId;

    @Schema(description = "답글 고유 아이디", example = "1")
    private Long parentCommentId;

    @Schema(description = "댓글 내용", example = "테스트용입니다.")
    private String content;

    @Schema(description = "댓글 생성 일시", example = "2025-08-01 21:03:52")
    private LocalDateTime createdAt;

    @Schema(description = "답글 목록")
    private List<CommentDTO> replies;
}
