package com.musai.musai.repository.community;

import com.musai.musai.entity.community.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    List<UserReport> findByReporterIdOrReportedUserId(Long reporterId, Long reportedUserId);
    void deleteByReporterIdOrReportedUserId(Long reporterId, Long reportedUserId);
}
