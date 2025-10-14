package com.musai.musai.service.community;

import com.musai.musai.dto.community.ReportRequest;
import com.musai.musai.entity.community.UserReport;
import com.musai.musai.repository.community.UserReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserReportService {

    private final UserReportRepository userReportRepository;

    public Long reportUser(Long reporterId, ReportRequest request) {
        Long reportedId = request.getReportedUserId();

        if (reporterId.equals(reportedId)) {
            throw new IllegalArgumentException("자신을 신고할 수 없습니다.");
        }

        UserReport report = UserReport.builder()
                .reporterId(reporterId)
                .reportedUserId(reportedId)
                .reason(request.getReason())
                .status("PENDING") // 초기 상태는 PENDING(대기)으로 설정
                .build();

        UserReport savedReport = userReportRepository.save(report);
        return savedReport.getReportId();
    }
}
