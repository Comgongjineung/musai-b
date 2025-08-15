package com.musai.musai.repository.exhibition;

import com.musai.musai.entity.exhibition.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
    boolean existsBySeqnum(Integer seqnum); // ✅ seqnum으로 중복 체크
    void deleteByEndDateBefore(String date);
    List<Exhibition> findByTitleContainingIgnoreCase(String keyword);
    List<Exhibition> findByPlaceContainingIgnoreCase(String place);
    
    @Query("SELECT e FROM Exhibition e WHERE e.gpsX IS NOT NULL AND e.gpsX != '' AND e.gpsY IS NOT NULL AND e.gpsY != ''")
    List<Exhibition> findExhibitionsWithGPS();
}
