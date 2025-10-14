package com.musai.musai.controller.community;

import com.musai.musai.dto.community.PostReportRequest;
import com.musai.musai.service.community.PostReportService;
import com.musai.musai.service.community.UserBlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/community/post-report")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "커뮤니티 게시물 신고", description = "게시물 신고 기능 API")
@RequiredArgsConstructor
public class PostReportController {

    private final PostReportService postReportService;
    private final UserBlockService userBlockService;

    @Operation(summary = "게시물 신고 등록", description = "특정 게시물을 신고 테이블에 등록합니다.")
    @PostMapping
    public ResponseEntity<String> reportPost(
            @AuthenticationPrincipal String userEmail,
            @RequestBody PostReportRequest request) {

        try {
            Long reporterId = userBlockService.getUserIdByEmail(userEmail);

            Long reportId = postReportService.reportPost(reporterId, request);

            return ResponseEntity.status(HttpStatus.CREATED).body("게시물 신고가 성공적으로 접수되었습니다. Report ID: " + reportId);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("게시물 신고 중 서버 오류가 발생했습니다.");
        }
    }
}
