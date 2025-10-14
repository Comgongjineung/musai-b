package com.musai.musai.entity.community;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_report")
public class UserReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId; // 신고자 ID

    @Column(name = "reported_user_id", nullable = false)
    private Long reportedUserId; // 신고당한 사용자 ID

    @Column(name = "reason", length = 255)
    private String reason; // 신고 사유

    @Column(name = "status", length = 50)
    private String status; // 처리 상태 (예: PENDING, RESOLVED)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public UserReport(Long reporterId, Long reportedUserId, String reason, String status) {
        this.reporterId = reporterId;
        this.reportedUserId = reportedUserId;
        this.reason = reason;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}
