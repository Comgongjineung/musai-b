package com.musai.musai.repository.bookmark;

import com.musai.musai.entity.bookmark.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);
    Bookmark findByBookmarkIdAndUserId(Long bookmarkId, Long userId);
    void deleteByBookmarkId(Long bookmarkId);
}
