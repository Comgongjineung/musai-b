package com.musai.musai.dto.exhibition;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExhibitionDTO {
    private Long exhi_id;      // 전시회 ID
    private String title;      // 전시 이름
    private String startDate;  // 시작 날짜
    private String endDate;    // 종료 날짜
    private String place;      // 장소
    private String realmName;  // 전시 장르
    private String thumbnail;  // 썸네일 이미지 주소
    private String gpsX;       // 위도
    private String gpsY;       // 경도
}
