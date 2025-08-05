package com.musai.musai.service.community;

import com.musai.musai.dto.community.LikeDTO;
import com.musai.musai.entity.community.Like;
import com.musai.musai.repository.community.LikeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeService {

    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    //공감 추가
    @Transactional
    public Like addLike(LikeDTO dto) {
        // 중복 좋아요 방지
        if (likeRepository.existsByPostIdAndUserId(dto.getPostId(), dto.getUserId())) {
            throw new IllegalStateException("이미 좋아요가 등록된 게시물입니다.");
        }

        Like postLike = Like.builder()
                .postId(dto.getPostId())
                .userId(dto.getUserId())
                .build();

        return likeRepository.save(postLike);
    }

    public Long getLikeCountByPostId(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    @Transactional
    public Like deleteLike(Long postId, Long userId) {
        Like existingLike = likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new IllegalStateException("해당 게시물에 등록된 공감이 없습니다."));

        likeRepository.deleteByPostIdAndUserId(postId, userId);
        
        return existingLike;
    }
}
