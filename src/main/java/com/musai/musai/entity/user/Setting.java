package com.musai.musai.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_setting")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_difficulty", nullable = false)
    private DefaultDifficulty defaultDiffiiculty = DefaultDifficulty.NORMAL;

    @Column(name = "allow_calarm", nullable = false)
    private Boolean allowCalarm = true;

    @Column(name = "allow_ralarm", nullable = false)
    private Boolean allowRalarm = true;
}
