package com.musai.musai.repository.community;

import com.musai.musai.entity.community.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    List<PostReport> findByReporterId(Long reporterId);
    void deleteByReporterId(Long reporterId);
}
