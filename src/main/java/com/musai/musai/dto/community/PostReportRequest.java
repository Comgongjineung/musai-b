package com.musai.musai.dto.community;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostReportRequest {
    private Long postId;
    private String reason;
}
