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
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "default_difficulty", nullable = false)
    private DefaultDifficulty defaultDiffiiculty = DefaultDifficulty.NORMAL;

    @Builder.Default
    @Column(name = "allow_calarm", nullable = false)
    private Boolean allowCalarm = true;

    @Builder.Default
    @Column(name = "allow_ralarm", nullable = false)
    private Boolean allowRalarm = true;
}
