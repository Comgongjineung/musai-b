package com.musai.musai.dto.exhibition;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.musai.musai.entity.exhibition.Exhibition;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExhibitionDTO {

    @JacksonXmlProperty(localName = "seq")
    private Long exhi_id; // ✅ API에서 오는 seq → DB의 seqnum으로 사용

    @JacksonXmlProperty(localName = "serviceName")
    private String serviceName;

    @JacksonXmlProperty(localName = "title")
    private String title;

    @JacksonXmlProperty(localName = "startDate")
    private String startDate;

    @JacksonXmlProperty(localName = "endDate")
    private String endDate;

    @JacksonXmlProperty(localName = "place")
    private String place;

    @JacksonXmlProperty(localName = "realmName")
    private String realmName;

    @JacksonXmlProperty(localName = "area")
    private String area;

    @JacksonXmlProperty(localName = "sigungu")
    private String sigungu;

    @JacksonXmlProperty(localName = "thumbnail")
    private String thumbnail;

    @JacksonXmlProperty(localName = "gpsX")
    private String gpsX;

    @JacksonXmlProperty(localName = "gpsY")
    private String gpsY;

    public Exhibition toEntity() {
        Exhibition ex = new Exhibition();

        // ✅ seqnum 매핑
        if (exhi_id != null) {
            ex.setSeqnum(exhi_id.intValue());
        }

        ex.setTitle(title);
        ex.setStartDate(startDate);
        ex.setEndDate(endDate);
        ex.setPlace(place);
        ex.setRealmName(realmName);
        ex.setThumbnail(thumbnail);
        ex.setGpsX(gpsX);
        ex.setGpsY(gpsY);

        return ex;
    }
}
