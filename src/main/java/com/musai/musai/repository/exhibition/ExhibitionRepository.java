package com.musai.musai.repository.exhibition;

import com.musai.musai.entity.exhibition.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
    boolean existsByExhiId(Long exhiId);
    void deleteByEndDateBefore(String date);

    boolean existsBySeqnum(Integer seqnum);
}
