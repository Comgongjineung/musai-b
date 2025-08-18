package com.musai.musai.entity.preferenceRecommend;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "preference")
@Getter
@Setter
public class PreferenceRecommend {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(columnDefinition = "json")
    private String preferences; // JSON 문자열 그대로 저장
}
