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
@Table(name = "post_report")
public class PostReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public PostReport(Long reporterId, Long postId, String reason, String status) {
        this.reporterId = reporterId;
        this.postId = postId;
        this.reason = reason;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}
