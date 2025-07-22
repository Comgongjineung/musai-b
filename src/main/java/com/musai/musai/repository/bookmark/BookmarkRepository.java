package com.musai.musai.repository.bookmark;

import com.musai.musai.entity.bookmark.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);
    Bookmark findByBookmarkIdAndUserId(Long bookmarkId, Long userId);
    void deleteByBookmarkIdAndUserId(Long bookmarkId, Long userId);
}
