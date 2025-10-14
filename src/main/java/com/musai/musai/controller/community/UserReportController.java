package com.musai.musai.controller.community;

import com.musai.musai.dto.community.ReportRequest;
import com.musai.musai.service.community.UserReportService;
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
@RequestMapping("/api/v1/community/report")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "커뮤니티 사용자 신고", description = "사용자 신고 기능 API")
@RequiredArgsConstructor
public class UserReportController {

    private final UserReportService userReportService;
    private final UserBlockService userBlockService; // UserBlockService를 주입받아 이메일로 ID를 찾는 로직 활용

    @Operation(summary = "사용자 신고 등록", description = "특정 사용자를 신고 테이블에 등록합니다.")
    @PostMapping
    public ResponseEntity<String> reportUser(
            @AuthenticationPrincipal String userEmail, // JWT에서 이메일(String)을 받아옴
            @RequestBody ReportRequest request) {

        try {
            // Service를 통해 이메일을 이용해 신고자(reporter)의 Long ID를 찾고 신고 로직 실행
            Long reporterId = userBlockService.getUserIdByEmail(userEmail);

            Long reportId = userReportService.reportUser(reporterId, request);

            return ResponseEntity.status(HttpStatus.CREATED).body("신고가 성공적으로 접수되었습니다. Report ID: " + reportId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            // userBlockService.getUserIdByEmail에서 발생할 수 있는 '사용자 없음' 예외 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("사용자 신고 중 서버 오류가 발생했습니다.");
        }
    }
}
