package com.musai.musai.controller.user;

import com.musai.musai.dto.community.PostDTO;
import com.musai.musai.dto.user.SettingDTO;
import com.musai.musai.dto.user.UserDTO;
import com.musai.musai.dto.user.UserErrorDTO;
import com.musai.musai.entity.user.DefaultDifficulty;
import com.musai.musai.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "사용자", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원 정보 조회", description = "회원 정보를 조회합니다.",
    responses = {
        @ApiResponse(
                responseCode = "200",
                description = "분석 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "서버 오류",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserErrorDTO.class)
                )
        )
    })
    @GetMapping("/read/{userId}")
    public ResponseEntity<UserDTO> readUser(@PathVariable Long userId) {
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "닉네임 중복 체크", description = "닉네임이 중복된 닉네임인지 확인합니다.")
    @GetMapping("/check/nickname")
    public ResponseEntity<?> checkNickname(
            @RequestParam(required = false) Long userId,
            @RequestParam String nickname) {
        try {
            userService.checkNickname(userId, nickname);
            return ResponseEntity.ok("사용 가능한 닉네임입니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보(닉네임, 프로필 사진)를 수정합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "분석 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserErrorDTO.class)
                            )
                    )
            })
    @PutMapping("/update")
    public ResponseEntity<?> updateUser (
            @RequestBody UserDTO userDTO) {
        try {
            UserDTO updateUser = userService.updateUser(userDTO);
            return ResponseEntity.ok(updateUser);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("이미 사용 중인 닉네임입니다")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다.")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<UserDTO> deleteUser(
            @PathVariable Long userId) {
        UserDTO deleteUser = userService.deleteUser(userId);
        return ResponseEntity.ok(deleteUser);
    }

    @Operation(summary = "사용자 설정 조회", description = "사용자 설정 정보를 조회합니다..")
    @GetMapping("/setting/{userId}")
    public ResponseEntity<SettingDTO> readSetting (
            @PathVariable Long userId) {
        SettingDTO setting = userService.getSettingById(userId);
        return ResponseEntity.ok(setting);
    }

    @Operation(summary = "난이도별 해설 기본값 수정", description = "난이도별 해설 기본값을 수정합니다.")
    @PutMapping("/difficulty/{userId}/{level}")
    public ResponseEntity<SettingDTO> updateLevel (
            @PathVariable Long userId,
            @PathVariable DefaultDifficulty level) {
        SettingDTO updateSet = userService.updateLevel(userId, level);
        return ResponseEntity.ok(updateSet);
    }

    @Operation(summary = "추천 알림 상태 변경", description = "추천 알림을 on/off 합니다.")
    @PutMapping("/alarm/recog/{userId}")
    public ResponseEntity<SettingDTO> updateRecogAlarm(
            @PathVariable Long userId) {
        SettingDTO updateSet = userService.updateRecogAlarm(userId);
        return ResponseEntity.ok(updateSet);
    }

    @Operation(summary = "커뮤니티 알림 상태 변경", description = "커뮤니티 알림을 on/off 합니다.")
    @PutMapping("/alarm/community/{userId}")
    public ResponseEntity<SettingDTO> updateCommunityAlarm(
            @PathVariable Long userId) {
        SettingDTO updateSet = userService.updateCommunityAlarm(userId);
        return ResponseEntity.ok(updateSet);
    }

    @Operation(summary = "내가 쓴 게시글 조회", description = "사용자가 쓴 게시글을 조회합니다.")
    @GetMapping("/post/{userId}")
    public ResponseEntity<List<PostDTO>> myPost(
            @PathVariable Long userId) {
        List<PostDTO> posts = userService.myPost(userId);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "내가 작성한 댓글 조회", description = "사용자가 댓글을 작성한 게시글을 조회합니다.")
    @GetMapping("/comment/{userId}")
    public ResponseEntity<List<PostDTO>> myComment(@PathVariable Long userId) {
        List<PostDTO> comments = userService.myComment(userId);
        return ResponseEntity.ok(comments);
    }
}
