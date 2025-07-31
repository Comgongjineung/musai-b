package com.musai.musai.service.post;

import com.musai.musai.dto.post.PostDto;
import com.musai.musai.entity.post.Post;
import com.musai.musai.repository.post.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    //게시글 업로드
    public PostDto createPost(PostDto postDto) {
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
        return PostDto.fromEntity(savedPost);
    }

    //게시글 삭제
    public void deletePost(Long postId){
        if(!postRepository.existsById(postId)){
            throw new IllegalArgumentException("포스트 아이디 " + postId + " 없음");
        }
        postRepository.deleteById(postId);
    }

    //전체 게시글 조회
    public List<PostDto> getAllPosts(){
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(PostDto::fromEntity)
                .collect(Collectors.toList());
    }

    //게시글 상세 조회(id로)
    public PostDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 게시물이 존재하지 않습니다."));

        return PostDto.fromEntity(post);
    }

    //게시글 수정
    public PostDto updatePost(Long postId, PostDto postDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 게시물이 존재하지 않습니다."));
        post.update(postDto.getTitle(), postDto.getContent(),
                postDto.getImage1(), postDto.getImage2(),
                postDto.getImage3(), postDto.getImage4());

        post.setUpdatedAt(LocalDateTime.now());

        return PostDto.fromEntity(postRepository.save(post));
    }
}