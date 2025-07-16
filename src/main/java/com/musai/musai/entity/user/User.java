package com.musai.musai.entity.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId; // PK

    @Column(name = "oauth_provider", length = 50, nullable = false)
    private String oauthProvider; // ex: "google", "kakao"

    @Column(name = "oauth_id", length = 100, nullable = false)
    private String oauthId; // 소셜 로그인에서 제공하는 유저 고유 ID

    @Column(length = 100, nullable = false, unique = true)
    private String email; // 이메일

    @Column(length = 200)
    private String nickname; // 닉네임

    @Column(name = "profile_image", columnDefinition = "TEXT")
    private String profileImage; // 프로필 이미지 URL

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성일

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일

    // ✅ 엔티티 저장 전에 자동 시간 설정
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
