package com.musai.musai.repository.community;

import com.musai.musai.entity.community.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    List<Like> findAllByUserId(Long userId);
}
