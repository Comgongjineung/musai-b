package com.musai.musai.repository.arts;

import com.musai.musai.entity.arts.Art;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtRepository extends JpaRepository<Art, Integer> {
    boolean existsByTitle(String title);

}

