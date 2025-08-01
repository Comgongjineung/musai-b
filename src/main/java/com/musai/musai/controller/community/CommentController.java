package com.musai.musai.controller.community;

import com.musai.musai.dto.community.CommentDTO;
import com.musai.musai.service.community.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "게시글 댓글 조회 (페이징 + 계층형)", description = "특정 게시글의 댓글을 페이징하여 조회합니다. 답글 구조가 포함됩니다.")
    @GetMapping("/readAll/{postId}")
    public ResponseEntity<Page<CommentDTO>> readCommentWithPaging(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page) {
        
        // 백엔드에서 페이징 설정을 고정
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<CommentDTO> comments = commentService.getHierarchicalCommentsWithPaging(postId, pageable);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "댓글 작성", description = "댓글을 작성합니다.")
    @PostMapping("/add")
    public ResponseEntity<CommentDTO> addComment(@RequestBody CommentDTO requestDTO) {
        CommentDTO comment = commentService.addComment(requestDTO);
        return ResponseEntity.ok(comment);
    }

    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    @PutMapping("/update/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long commentId, @RequestBody CommentDTO requestDTO) {
        CommentDTO updateComment = commentService.updateComment(commentId, requestDTO);
        return ResponseEntity.ok(updateComment);
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<CommentDTO> deleteComment(@PathVariable Long commentId) {
        CommentDTO deleteComment = commentService.deleteComment(commentId);
        return ResponseEntity.ok(deleteComment);
    }
}
