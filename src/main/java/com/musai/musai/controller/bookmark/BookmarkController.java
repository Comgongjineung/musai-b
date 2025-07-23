package com.musai.musai.controller.bookmark;

import com.musai.musai.dto.bookmark.BookmarkDTO;
import com.musai.musai.service.bookmark.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmark")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "특정 사용자의 전체 북마크 조회", description = "북마크 목록을 조회합니다.")
    @GetMapping("/readAll/{userId}")
    public ResponseEntity<List<BookmarkDTO>> readBookmarkList(@PathVariable Long userId) {
        List<BookmarkDTO> bookmarks = bookmarkService.getAllBookmarksByUser(userId);
        return ResponseEntity.ok(bookmarks);
    }

    @Operation(summary = "특정 사용자의 특정 북마크 조회", description = "북마크를 상세 조회합니다.")
    @GetMapping("/read/{bookmarkId}/{userId}")
    public ResponseEntity<BookmarkDTO> readBookmark(@PathVariable Long bookmarkId, @PathVariable Long userId) {
        BookmarkDTO bookmark = bookmarkService.getBookmark(bookmarkId, userId);
        if (bookmark == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bookmark);
    }

    @Operation(summary = "북마크 추가", description = "북마크를 추가합니다.")
    @PostMapping("/add")
    public ResponseEntity<Void> addBookmark(@RequestBody BookmarkDTO dto) {
        bookmarkService.addBookmark(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "북마크 삭제", description = "북마크를 삭제합니다.")
    @DeleteMapping("/delete/{bookmarkId}/{userId}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long bookmarkId, @PathVariable Long userId) {
        bookmarkService.deleteBookmark(bookmarkId, userId);
        return ResponseEntity.ok().build();
    }
}
