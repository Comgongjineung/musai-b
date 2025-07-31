package com.musai.musai.controller.post;

import com.musai.musai.dto.post.PostDto;
import com.musai.musai.service.post.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    //글 작성
    @PostMapping("/add")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        PostDto createdPost = postService.createPost(postDto);
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
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<PostDto> postList = postService.getAllPosts();
        return ResponseEntity.ok(postList);
    }

    //게시글 상세 조회(id로)
    @GetMapping("/detail/{postId}")
    public ResponseEntity<PostDto> getPostDetail(@PathVariable Long postId) {
        PostDto postDto = postService.getPostById(postId);
        return ResponseEntity.ok(postDto);
    }

    //게시글 수정
    @PutMapping("/update/{postId}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long postId,
            @RequestBody PostDto postDto){
        PostDto updatedPost = postService.updatePost(postId, postDto);
        return ResponseEntity.ok(updatedPost);
    }

}