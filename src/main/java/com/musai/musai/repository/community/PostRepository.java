package com.musai.musai.repository.community;

import com.musai.musai.dto.community.PostDTO;
import com.musai.musai.entity.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
}
