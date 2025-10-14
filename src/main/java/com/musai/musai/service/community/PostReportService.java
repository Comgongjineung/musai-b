package com.musai.musai.service.community;

import com.musai.musai.dto.community.PostReportRequest;
import com.musai.musai.entity.community.PostReport;
import com.musai.musai.repository.community.PostReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostReportService {

    private final PostReportRepository postReportRepository;

    public Long reportPost(Long reporterId, PostReportRequest request) {

        // 신고하려는 게시물 ID와 사유가 유효한지 검증 (필요하다면 PostRepository를 주입받아 postId 존재 여부 확인 가능)

        PostReport report = PostReport.builder()
                .reporterId(reporterId)
                .postId(request.getPostId())
                .reason(request.getReason())
                .status("PENDING") // 초기 상태는 PENDING(대기)으로 설정
                .build();

        PostReport savedReport = postReportRepository.save(report);
        return savedReport.getReportId();
    }
}
