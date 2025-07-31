package com.musai.musai.dto.post;

import com.musai.musai.entity.post.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long postId;
    private Long userId;
    private String title;
    private String content;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostDto fromEntity(Post post) {
        PostDto dto = new PostDto();
        dto.setPostId(post.getPostId());
        dto.setUserId(post.getUserId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setImage1(post.getImage1());
        dto.setImage2(post.getImage2());
        dto.setImage3(post.getImage3());
        dto.setImage4(post.getImage4());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        return dto;
    }
}
