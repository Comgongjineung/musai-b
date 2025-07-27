package com.musai.musai.dto.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "티켓")
public class TicketDTO {
    @Schema(description = "티켓 고유 아이디", example = "1")
    private Long ticketId;

    @Schema(description = "사용자 고유 아이디", example = "1")
    private Long userId;

    @Schema(description = "티켓 생성 날짜", example = "2025-07-22T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "티켓 이미지", example = "ticket.jpg")
    private String ticketImage;

    @Schema(description = "작품명", example = "별이 빛나는 밤")
    private String title;

    @Schema(description = "작가명", example = "빈센트 반고흐")
    private String artist;

    @Schema(description = "전시관", example = "예술의 전당")
    private String place;
}
