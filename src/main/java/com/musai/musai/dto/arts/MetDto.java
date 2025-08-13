package com.musai.musai.dto.arts;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class MetDto {
    private int total;
    private List<Long> objectIDs;  // JSON 키와 정확히 일치해야 합니다
}