package com.musai.musai.dto.exhibition;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.musai.musai.entity.exhibition.Exhibition;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExhibitionDTO {
    @JacksonXmlProperty(localName = "seq")
    private Long exhi_id;      // 전시회 ID (API에서 seq로 옴)

    private String title;
    private String startDate;
    private String endDate;
    private String place;
    private String realmName;
    private String thumbnail;
    private String gpsX;
    private String gpsY;

    // API에 seqnum 필드 없으니 제외하거나 별도로 관리

    public Exhibition toEntity() {
        Exhibition ex = new Exhibition();
        ex.setExhiId(exhi_id);
        ex.setTitle(title);
        ex.setStartDate(startDate);
        ex.setEndDate(endDate);
        ex.setPlace(place);
        ex.setRealmName(realmName);
        ex.setThumbnail(thumbnail);
        ex.setGpsX(gpsX);
        ex.setGpsY(gpsY);
        // seqnum는 별도 처리 필요
        return ex;
    }
}
