package com.musai.musai.repository.ar;

import com.musai.musai.entity.ar.ArArt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArArtRepository extends JpaRepository<ArArt, Long> {
    
    Optional<ArArt> findByTargetId(String targetId);
    
    boolean existsByTargetId(String targetId);
}
