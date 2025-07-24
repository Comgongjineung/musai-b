package com.musai.musai.controller.user;

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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
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
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDTO> updateUser (
            @PathVariable Long userId,
            @RequestBody UserDTO userDTO) {
        UserDTO updateUser = userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(updateUser);
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
}
