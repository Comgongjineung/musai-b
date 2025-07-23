package com.musai.musai.dto.user;

import com.musai.musai.entity.user.DefaultDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "설정 정보 조회")
public class SettingDTO {
    private Long userId;
    private DefaultDifficulty defaultDifiiculty;
    private Boolean allowCAlarm;
    private Boolean allowRAlarm;
}
