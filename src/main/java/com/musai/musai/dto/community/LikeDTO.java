package com.musai.musai.dto.community;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class LikeDTO {

    private Long postId;
    private Long userId;

    @Builder
    public LikeDTO(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }
}
