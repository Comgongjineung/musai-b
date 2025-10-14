package com.musai.musai.repository.community;

import com.musai.musai.entity.community.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    Page<Comment> findByPostId(Long postId, Pageable pageable);
    Optional<Comment> findByCommentId(Long commentId);
    List<Comment> findByUserId(Long userId);
    Long countByPostId(Long postId);
    void deleteByPostId(Long postId);
    void deleteByPostIdAndParentCommentIdIsNotNull(Long postId);
    void deleteByPostIdAndParentCommentIdIsNull(Long postId);

    List<Comment> findByPostIdAndUserIdNotIn(Long postId, List<Long> blockedUserIds);
}
