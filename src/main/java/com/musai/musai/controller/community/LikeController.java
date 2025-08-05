package com.musai.musai.controller.community;

import com.musai.musai.dto.community.LikeDTO;
import com.musai.musai.entity.community.Like;
import com.musai.musai.service.community.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @Operation(summary = "공감 등록", description = "게시글에 공감을 추가합니다.")
    @PostMapping("/add")
    public ResponseEntity<String> addLike(@RequestBody LikeDTO dto) {
        try {
            Like savedLike = likeService.addLike(dto);
            return ResponseEntity.ok("공감이 등록되었습니다. likeId: " + savedLike.getId());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "공감 조회", description = "내가 공감한 게시글을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<List<Like>> getLikesByUser(@RequestParam Long userId) {
        List<Like> likes = likeService.getLikesByUserId(userId);
        return ResponseEntity.ok(likes);
    }

    @Operation(summary = "공감 취소", description = "공감을 취소합니다.")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteLike(@RequestBody LikeDTO dto) {
        try {
            Like deleteLikes = likeService.deleteLike(dto.getPostId(), dto.getUserId());
            return ResponseEntity.ok("공감이 취소되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
