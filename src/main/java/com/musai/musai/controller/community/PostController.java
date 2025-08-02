package com.musai.musai.controller.community;

import com.musai.musai.dto.community.PostDTO;
import com.musai.musai.service.community.PostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@SecurityRequirement(name = "bearerAuth")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    //글 작성
    @PostMapping("/add")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDto) {
        PostDTO createdPost = postService.createPost(postDto);
        return ResponseEntity.ok(createdPost);
        //반환값 postDto로 반환 부탁!
    }

    //게시글 삭제
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("게시물 삭제 성공");
    }

    //전체 게시글 조회
    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> postList = postService.getAllPosts();
        return ResponseEntity.ok(postList);
    }

    //게시글 상세 조회(id로)
    @GetMapping("/detail/{postId}")
    public ResponseEntity<PostDTO> getPostDetail(@PathVariable Long postId) {
        PostDTO postDto = postService.getPostById(postId);
        return ResponseEntity.ok(postDto);
    }

    //게시글 수정
    @PutMapping("/update/{postId}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long postId,
            @RequestBody PostDTO postDto){
        PostDTO updatedPost = postService.updatePost(postId, postDto);
        return ResponseEntity.ok(updatedPost);
    }

    //게시물 검색
    @GetMapping("/search")
    public ResponseEntity<List<PostDTO>> searchPosts(@RequestParam String keyword) {
        List<PostDTO> searchResults = postService.searchPosts(keyword);
        return ResponseEntity.ok(searchResults);
    }

}