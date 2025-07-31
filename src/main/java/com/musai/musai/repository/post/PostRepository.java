package com.musai.musai.repository.post;

import com.musai.musai.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
