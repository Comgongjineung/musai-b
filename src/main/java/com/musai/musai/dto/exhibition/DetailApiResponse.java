package com.musai.musai.dto.exhibition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DetailApiResponse {

    private Header header;
    private Body body;

    @Data
    public static class Header {
        @JacksonXmlProperty(localName = "resultCode")
        private String resultCode;

        @JacksonXmlProperty(localName = "resultMsg")
        private String resultMsg;
    }

    @Data
    public static class Body {
        private Items items;
    }

    @Data
    public static class Items {
        private Item item;
    }

    @Data
    public static class Item {
        @JacksonXmlProperty(localName = "seq")
        private String seq;

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

        @JacksonXmlProperty(localName = "price")
        private String price;

        @JacksonXmlProperty(localName = "contents1")
        private String contents1;

        @JacksonXmlProperty(localName = "url")
        private String url;

        @JacksonXmlProperty(localName = "phone")
        private String phone;

        @JacksonXmlProperty(localName = "gpsX")
        private String gpsX;

        @JacksonXmlProperty(localName = "gpsY")
        private String gpsY;

        @JacksonXmlProperty(localName = "imgUrl")
        private String imgUrl;

        @JacksonXmlProperty(localName = "placeUrl")
        private String placeUrl;

        @JacksonXmlProperty(localName = "placeAddr")
        private String placeAddr;

        @JacksonXmlProperty(localName = "placeSeq")
        private String placeSeq;

        @JacksonXmlProperty(localName = "sigungu")
        private String sigungu;
    }
}
