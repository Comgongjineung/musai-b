package com.musai.musai.repository.arts;

import com.musai.musai.entity.arts.Met;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetRepository extends JpaRepository<Met, Long> {
}
