package com.musai.musai.repository.arts;

import com.musai.musai.entity.arts.Art;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtRepository extends JpaRepository<Art, Integer> {
    boolean existsByTitle(String title);

    // 특정 style에서 랜덤 N개
    @Query(value = "SELECT * FROM arts WHERE style = :style ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Art> findRandomByStyle(String style, int count);

    // 랜덤 N개 (스타일 무관)
    @Query(value = "SELECT * FROM arts ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Art> findRandomAll(int count);

}

