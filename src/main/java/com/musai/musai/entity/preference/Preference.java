package com.musai.musai.entity.preference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "preference")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Preference {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "preferences", columnDefinition = "JSON")
    private String preferences;
}
