package com.musai.musai.entity.ar;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "ar_art")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArArt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "art_id")
    private Long artId;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "target_id", nullable = false, unique = true)
    private String targetId;
}
