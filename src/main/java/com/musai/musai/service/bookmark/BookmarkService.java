package com.musai.musai.service.bookmark;

import com.musai.musai.dto.bookmark.BookmarkDTO;
import com.musai.musai.entity.bookmark.Bookmark;
import com.musai.musai.repository.bookmark.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public List<BookmarkDTO> getAllBookmarksByUser(Long userId) {
        return bookmarkRepository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public BookmarkDTO getBookmark(Long bookmarkId, Long userId) {
        Bookmark bookmark = bookmarkRepository.findByBookmarkIdAndUserId(bookmarkId, userId);
        return bookmark != null ? toDTO(bookmark) : null;
    }

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
