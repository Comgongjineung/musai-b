package com.musai.musai.dto.community;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserBlockResponse {
    private Long blockId;
    private Long blockedUserId;
}