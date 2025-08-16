package com.musai.musai.repository.ar;

import com.musai.musai.entity.ar.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    
    List<Point> findByArArt_ArtId(Long artId);
    
    void deleteByArArt_ArtId(Long artId);
}
