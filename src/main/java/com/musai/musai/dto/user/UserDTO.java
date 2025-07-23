package com.musai.musai.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema (description = "회원 정보 조회")
public class UserDTO {
    @Schema(description = "회원 아이디 자동 배정", example = "4")
    private Long userId;
    @Schema(description = "회원 이메일", example = "musai@musai.com")
    private String email;
    @Schema(description = "회원 닉네임", example = "무사이")
    private String nickname;
    @Schema(description = "프로필 사진", example = "12345.jpg")
    private String profileImage;
}
