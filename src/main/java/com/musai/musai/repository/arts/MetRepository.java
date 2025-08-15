package com.musai.musai.repository.arts;

import com.musai.musai.entity.arts.Met;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetRepository extends JpaRepository<Met, Long> {
    List<Met> findByMetIdBetween(Long start, Long end);
}
