package com.musai.musai.dto.community;

import lombok.Data;

@Data
public class UserBlockRequest {
    private Long blockedUserId;  // 차단 대상
}