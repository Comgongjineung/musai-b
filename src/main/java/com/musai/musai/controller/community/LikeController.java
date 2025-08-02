package com.musai.musai.controller.community;

import com.musai.musai.dto.community.LikeDTO;
import com.musai.musai.entity.community.Like;
import com.musai.musai.service.community.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    public ResponseEntity<String> addLike(@RequestBody LikeDTO dto) {
        try {
            Like savedLike = likeService.addLike(dto);
            return ResponseEntity.ok("좋아요가 등록되었습니다. likeId: " + savedLike.getId());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<Like>> getLikesByUser(@RequestParam Long userId) {
        List<Like> likes = likeService.getLikesByUserId(userId);
        return ResponseEntity.ok(likes);
    }
}
