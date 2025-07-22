package com.musai.musai.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "오류 응답 DTO")
public class UserErrorDTO {
    @Schema(description = "HTTP 상태 코드", example = "500")
    private int status;

    @Schema(description = "오류 이름", example = "Internal Server Error")
    private String error;

    @Schema(description = "오류 메시지 상세 내용", example = "서버 오류입니다.")
    private String message;
}
