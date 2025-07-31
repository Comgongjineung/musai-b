package com.musai.musai.entity.post;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long postId;

    private Long userId;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String image1;
    @Column(columnDefinition = "TEXT")
    private String image2;
    @Column(columnDefinition = "TEXT")
    private String image3;
    @Column(columnDefinition = "TEXT")
    private String image4;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void update(String title, String content,
                       String image1, String image2, String image3, String image4) {
        this.title = title;
        this.content = content;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
    }
}

