package com.musai.musai.controller.community;

import com.musai.musai.dto.community.UserBlockRequest;
import com.musai.musai.dto.community.UserBlockResponse;
import com.musai.musai.service.community.UserBlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/community/blocks")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "커뮤니티 사용자 차단", description = "커뮤니티 사용자 차단 기능 API")
@RequiredArgsConstructor
public class UserBlockController {

    private final UserBlockService userBlockService;

    @Operation(summary = "사용자 차단 등록", description = "특정 사용자를 차단 목록에 추가합니다.")
    @PostMapping("/add")
    public ResponseEntity<String> blockUser(
            @AuthenticationPrincipal String userEmail,
            @RequestBody UserBlockRequest request) {

        try {
            UserBlockResponse response = userBlockService.blockUserByEmail(userEmail, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("사용자 차단 성공. blockId: " + response.getBlockId());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("사용자 차단 중 서버 오류 발생.");
        }
    }


    @Operation(summary = "사용자 차단 해제", description = "특정 사용자의 차단을 해제합니다.")
    @DeleteMapping("/delete/{blockedUserId}")
    public ResponseEntity<String> unblockUser(
            @AuthenticationPrincipal String userEmail,
            @PathVariable Long blockedUserId) {

        try {
            userBlockService.deleteBlockByEmail(userEmail, blockedUserId);
            return ResponseEntity.ok("사용자 차단이 취소되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("사용자 차단 해제 중 서버 오류 발생.");
        }
    }

    @Operation(summary = "사용자 차단 목록 조회", description = "현재 로그인한 사용자가 차단한 사용자들의 목록(ID)을 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<Long>> getBlockedUsersList(@AuthenticationPrincipal String userEmail) {
        try {
            List<Long> blockedUserIds = userBlockService.getBlockedUsersByEmail(userEmail);
            return ResponseEntity.ok(blockedUserIds);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
