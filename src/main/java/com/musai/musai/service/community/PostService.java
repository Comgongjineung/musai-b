package com.musai.musai.service.community;

import com.musai.musai.dto.community.PostDTO;
import com.musai.musai.dto.community.PostRequestDTO;
import com.musai.musai.dto.community.PostUpdateDTO;
import com.musai.musai.entity.community.Post;
import com.musai.musai.repository.community.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final LikeService likeService;
    private final CommentService commentService;

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
}