package com.musai.musai.dto.alarm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "알림")
public class AlarmDTO {
    @Schema(description = "알림 고유 아이디")
    private Long alarmId;
    @Schema(description = "사용자 고유 아이디")
    private Long userId;
    @Schema(description = "알림 타입")
    private String type;
    @Schema(description = "알림 제목")
    private String title;
    @Schema(description = "알림 내용")
    private String content;
    @Schema(description = "알림 읽음 여부")
    private Boolean isRead;
    @Schema(description = "알림 생성 날짜")
    private LocalDateTime createdAt;
}
