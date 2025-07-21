package com.musai.musai.entity.exhibition;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exhibition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exhibition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhi_id")
    private Long exhiId;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String startDate;

    @Column(columnDefinition = "TEXT")
    private String endDate;

    @Column(columnDefinition = "TEXT")
    private String place;

    @Column(columnDefinition = "TEXT")
    private String realmName;

    @Column(columnDefinition = "TEXT")
    private String thumbnail;

    @Column(columnDefinition = "TEXT")
    private String gpsX;

    @Column(columnDefinition = "TEXT")
    private String gpsY;
}
