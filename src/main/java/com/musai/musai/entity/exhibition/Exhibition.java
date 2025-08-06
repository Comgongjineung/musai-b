package com.musai.musai.entity.exhibition;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exhibition", uniqueConstraints = {@UniqueConstraint(columnNames = "seqnum")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exhibition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhi_id")
    private Long exhiId; // DB 자동 증가 PK

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", name="start_date")
    private String startDate;

    @Column(columnDefinition = "TEXT", name="end_date")
    private String endDate;

    @Column(columnDefinition = "TEXT")
    private String place;

    @Column(columnDefinition = "TEXT", name="realm_name")
    private String realmName;

    @Column(columnDefinition = "TEXT")
    private String thumbnail;

    @Column(columnDefinition = "TEXT", name="gps_x")
    private String gpsX;

    @Column(columnDefinition = "TEXT", name="gps_y")
    private String gpsY;

    @Column(nullable = false, unique = true)
    private Integer seqnum;

    @Column(columnDefinition = "TEXT", name = "place_url")
    private String placeUrl;
}
