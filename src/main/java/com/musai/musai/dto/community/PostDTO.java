package com.musai.musai.dto.community;

import com.musai.musai.entity.community.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDTO {
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
    private Long likeCount;
    private Long commentCount;

    public static PostDTO fromEntity(Post post) {
        PostDTO dto = new PostDTO();
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

    public static PostDTO fromEntity(Post post, Long likeCount, Long commentCount) {
        PostDTO dto = fromEntity(post);
        dto.setLikeCount(likeCount);
        dto.setCommentCount(commentCount);
        return dto;
    }
}
