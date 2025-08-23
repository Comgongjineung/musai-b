package com.musai.musai.repository.arts;

import com.musai.musai.entity.arts.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query(value = "SELECT * FROM image WHERE style = :style ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Image> findRandomByStyle(String style, int count);
}
