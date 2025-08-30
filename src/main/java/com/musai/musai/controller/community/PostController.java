package com.musai.musai.controller.community;

import com.musai.musai.dto.community.PostDTO;
import com.musai.musai.dto.community.PostRequestDTO;
import com.musai.musai.dto.community.PostUpdateDTO;
import com.musai.musai.dto.community.ImageUploadResponseDTO;
import com.musai.musai.service.community.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/post")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "커뮤니티 게시글", description = "커뮤니티 게시글 기능 API")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "게시글 작성", description = "게시글을 작성합니다.")
    @PostMapping("/add")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostRequestDTO postRequestDto) {
        PostDTO createdPost = postService.createPost(postRequestDto);
        return ResponseEntity.ok(createdPost);
    }

    @Operation(summary = "게시글 이미지 업로드", description = "게시글에 첨부할 이미지를 S3에 업로드하고 URL을 반환받습니다.")
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponseDTO> uploadImage(
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file) {
        
        ImageUploadResponseDTO response = postService.uploadImage(file);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("게시물 삭제 성공");
    }

    @Operation(summary = "게시글 전체 조회", description = "게시글 목록을 전체 조회합니다.")
    @GetMapping("/readAll")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> postList = postService.getAllPosts();
        return ResponseEntity.ok(postList);
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글을 상세 조회합니다.")
    @GetMapping("/detail/{postId}")
    public ResponseEntity<PostDTO> getPostDetail(@PathVariable Long postId) {
        PostDTO postDto = postService.getPostById(postId);
        return ResponseEntity.ok(postDto);
    }

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @PutMapping("/update/{postId}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateDTO postUpdateDTO){
        PostDTO updatedPost = postService.updatePost(postId, postUpdateDTO);
        return ResponseEntity.ok(updatedPost);
    }

    @Operation(summary = "게시글 검색", description = "게시글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<PostDTO>> searchPosts(@RequestParam String keyword) {
        List<PostDTO> searchResults = postService.searchPosts(keyword);
        return ResponseEntity.ok(searchResults);
    }

}