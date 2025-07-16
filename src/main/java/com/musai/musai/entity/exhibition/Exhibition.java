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

    @Column(length = 200, nullable = false)
    private String title;

    @Column(length = 100)
    private String host;

    @Column(length = 100)
    private String organization;

    @Column(length = 100)
    private String genre;

    @Column(length = 100)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String guide;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String duration;

    @Column(length = 255)
    private String period;

    @Column(length = 255)
    private String time;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String pageUrl;
}
