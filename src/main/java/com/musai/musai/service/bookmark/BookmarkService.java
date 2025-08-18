package com.musai.musai.service.bookmark;

import com.musai.musai.dto.bookmark.BookmarkDTO;
import com.musai.musai.entity.bookmark.Bookmark;
import com.musai.musai.repository.bookmark.BookmarkRepository;
import com.musai.musai.service.preference.PreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PreferenceService preferenceService;

    public List<BookmarkDTO> getAllBookmarksByUser(Long userId) {
        return bookmarkRepository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<BookmarkDTO> getBookmarksByStyle(Long userId, String style) {
        return bookmarkRepository.findByUserIdAndStyle(userId, style)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public BookmarkDTO getBookmark(Long bookmarkId, Long userId) {
        Bookmark bookmark = bookmarkRepository.findByBookmarkIdAndUserId(bookmarkId, userId);
        return bookmark != null ? toDTO(bookmark) : null;
    }

    @Transactional
    public BookmarkDTO addBookmark(BookmarkDTO dto) {
        Bookmark bookmark = Bookmark.builder()
                .userId(dto.getUserId())
                .title(dto.getTitle())
                .artist(dto.getArtist())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .style(dto.getStyle())
                .build();
        Bookmark saved = bookmarkRepository.save(bookmark);

        if (saved.getStyle() != null && !saved.getStyle().trim().isEmpty()) {
            try {
                preferenceService.increaseStyleScore(saved.getUserId(), saved.getStyle(), 1);
                log.info("사용자 {}의 {} 예술사조 선호도가 증가되었습니다.", saved.getUserId(), saved.getStyle());
            } catch (Exception e) {
                log.error("선호도 증가 중 오류 발생: {}", e.getMessage());
            }
        }
        
        return toDTO(saved);
    }
    
    @Transactional
    public BookmarkDTO deleteBookmark(Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElse(null);
        if (bookmark == null) {
            return null;
        }

        bookmarkRepository.deleteByBookmarkId(bookmarkId);
        return toDTO(bookmark);
    }

    private BookmarkDTO toDTO(Bookmark bookmark) {
        BookmarkDTO dto = new BookmarkDTO();
        dto.setBookmarkId(bookmark.getBookmarkId());
        dto.setUserId(bookmark.getUserId());
        dto.setTitle(bookmark.getTitle());
        dto.setArtist(bookmark.getArtist());
        dto.setDescription(bookmark.getDescription());
        dto.setImageUrl(bookmark.getImageUrl());
        dto.setStyle(bookmark.getStyle());
        return dto;
    }
}
