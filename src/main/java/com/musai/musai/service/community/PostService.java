package com.musai.musai.service.community;

import com.musai.musai.dto.community.PostDTO;
import com.musai.musai.entity.community.Post;
import com.musai.musai.repository.community.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    //게시글 업로드
    public PostDTO createPost(PostDTO postDto) {
        Post post = Post.builder()
                .userId(postDto.getUserId())
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .image1(postDto.getImage1())
                .image2(postDto.getImage2())
                .image3(postDto.getImage3())
                .image4(postDto.getImage4())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Post savedPost = postRepository.save(post);
        return PostDTO.fromEntity(savedPost);
    }

    //게시글 삭제
    public void deletePost(Long postId){
        if(!postRepository.existsById(postId)){
            throw new IllegalArgumentException("포스트 아이디 " + postId + " 없음");
        }
        postRepository.deleteById(postId);
    }

    //전체 게시글 조회
    public List<PostDTO> getAllPosts(){
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(PostDTO::fromEntity)
                .collect(Collectors.toList());
    }

    //게시글 상세 조회(id로)
    public PostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 게시물이 존재하지 않습니다."));

        return PostDTO.fromEntity(post);
    }

    //게시글 수정
    public PostDTO updatePost(Long postId, PostDTO postDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 게시물이 존재하지 않습니다."));
        post.update(postDto.getTitle(), postDto.getContent(),
                postDto.getImage1(), postDto.getImage2(),
                postDto.getImage3(), postDto.getImage4());

        post.setUpdatedAt(LocalDateTime.now());

        return PostDTO.fromEntity(postRepository.save(post));
    }

    //게시물 검색
    public List<PostDTO> searchPosts(String keyword) {
        List<Post> posts = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword);
        return posts.stream()
                .map(PostDTO::fromEntity)
                .collect(Collectors.toList());
    }
}