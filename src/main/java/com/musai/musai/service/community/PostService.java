package com.musai.musai.service.community;

import com.musai.musai.dto.community.PostDTO;
import com.musai.musai.dto.community.PostRequestDTO;
import com.musai.musai.dto.community.PostUpdateDTO;
import com.musai.musai.dto.community.ImageUploadResponseDTO;
import com.musai.musai.entity.community.Post;
import com.musai.musai.repository.community.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final LikeService likeService;
    private final CommentService commentService;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    //게시글 업로드
    public PostDTO createPost(PostRequestDTO postRequestDto) {
        Post post = Post.builder()
                .userId(postRequestDto.getUserId())
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .image1(postRequestDto.getImage1())
                .image2(postRequestDto.getImage2())
                .image3(postRequestDto.getImage3())
                .image4(postRequestDto.getImage4())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Post savedPost = postRepository.save(post);
        return PostDTO.fromEntity(savedPost, 0L, 0L);
    }

    //게시글 삭제
    public void deletePost(Long postId){
        if(!postRepository.existsById(postId)){
            throw new IllegalArgumentException("포스트 아이디 " + postId + " 없음");
        }

        likeService.deleteAllByPostId(postId);
        commentService.deleteAllByPostId(postId);
        postRepository.deleteById(postId);
    }

    //전체 게시글 조회
    public List<PostDTO> getAllPosts(){
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(post -> {
                    Long likeCount = likeService.getLikeCountByPostId(post.getPostId());
                    Long commentCount = commentService.getCommentCountByPostId(post.getPostId());
                    return PostDTO.fromEntity(post, likeCount, commentCount);
                })
                .collect(Collectors.toList());
    }

    //게시글 상세 조회
    public PostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 게시물이 존재하지 않습니다."));

        Long likeCount = likeService.getLikeCountByPostId(postId);
        Long commentCount = commentService.getCommentCountByPostId(postId);
        return PostDTO.fromEntity(post, likeCount, commentCount);
    }

    //게시글 수정
    public PostDTO updatePost(Long postId, PostUpdateDTO postUpdateDTO) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 게시물이 존재하지 않습니다."));
        post.update(postUpdateDTO.getTitle(), postUpdateDTO.getContent(),
                postUpdateDTO.getImage1(), postUpdateDTO.getImage2(),
                postUpdateDTO.getImage3(), postUpdateDTO.getImage4());

        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);
        Long likeCount = likeService.getLikeCountByPostId(postId);
        Long commentCount = commentService.getCommentCountByPostId(postId);
        return PostDTO.fromEntity(savedPost, likeCount, commentCount);
    }

    //게시물 검색
    public List<PostDTO> searchPosts(String keyword) {
        List<Post> posts = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword);
        return posts.stream()
                .map(post -> {
                    Long likeCount = likeService.getLikeCountByPostId(post.getPostId());
                    Long commentCount = commentService.getCommentCountByPostId(post.getPostId());
                    return PostDTO.fromEntity(post, likeCount, commentCount);
                })
                .collect(Collectors.toList());
    }

    //게시글 이미지 업로드
    public ImageUploadResponseDTO uploadImage(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ImageUploadResponseDTO.builder()
                        .success(false)
                        .message("업로드할 파일이 없습니다.")
                        .build();
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ImageUploadResponseDTO.builder()
                        .success(false)
                        .message("이미지 파일만 업로드 가능합니다.")
                        .build();
            }

            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String fileName = "community/post/" + UUID.randomUUID().toString() + fileExtension;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            if (response.sdkHttpResponse().isSuccessful()) {
                String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", 
                        bucketName, region, fileName);

                log.info("게시글 이미지 업로드 성공: {}", imageUrl);

                return ImageUploadResponseDTO.builder()
                        .success(true)
                        .imageUrl(imageUrl)
                        .fileName(fileName)
                        .message("이미지 업로드가 완료되었습니다.")
                        .build();
            } else {
                log.error("S3 업로드 실패: {}", response.sdkHttpResponse().statusCode());
                return ImageUploadResponseDTO.builder()
                        .success(false)
                        .message("이미지 업로드에 실패했습니다.")
                        .build();
            }

        } catch (IOException e) {
            log.error("이미지 업로드 중 오류 발생", e);
            return ImageUploadResponseDTO.builder()
                    .success(false)
                    .message("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생", e);
            return ImageUploadResponseDTO.builder()
                    .success(false)
                    .message("예상치 못한 오류가 발생했습니다.")
                    .build();
        }
    }
}