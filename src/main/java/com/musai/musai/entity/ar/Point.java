package com.musai.musai.entity.ar;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Entity
@Table(name = "points")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Point {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_id", nullable = false)
    private ArArt arArt;
    
    @Column(name = "x", nullable = false, precision = 5, scale = 4)
    private BigDecimal x;
    
    @Column(name = "y", nullable = false, precision = 5, scale = 4)
    private BigDecimal y;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
