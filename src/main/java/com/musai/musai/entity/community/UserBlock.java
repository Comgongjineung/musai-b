package com.musai.musai.entity.community;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_block",
        uniqueConstraints = @UniqueConstraint(columnNames = {"blocker_id", "blocked_user_id"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blockId;

    @Column(name = "blocker_id", nullable = false)
    private Long blockerId;

    @Column(name = "blocked_user_id", nullable = false)
    private Long blockedUserId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}