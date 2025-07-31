package com.musai.musai.entity.alarm;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Table(name = "alarm")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long alarmId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "type")
    private String type;

    @Column (name = "is_read")
    private Boolean isRead;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
