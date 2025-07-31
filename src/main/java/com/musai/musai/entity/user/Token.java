package com.musai.musai.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Table(name = "user_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {
    @Id
    @Column (name = "user_id")
    private Long userId;

    @Column(name = "token", columnDefinition = "TEXT")
    private String token;
}
