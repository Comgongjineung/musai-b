package com.musai.musai.repository.community;

import com.musai.musai.entity.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
