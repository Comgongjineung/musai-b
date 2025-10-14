package com.musai.musai.dto.community;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequest {
    private Long reportedUserId; // 신고당하는 사용자 ID (필수)
    private String reason;       // 신고 사유 (필수)
}
