package com.musai.musai.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.musai.musai.dto.exhibition.ExhibitionDTO;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ExhibitionApiResponse {

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
        @JacksonXmlProperty(localName = "totalCount")
        private int totalCount;

        @JacksonXmlProperty(localName = "PageNo")
        private int pageNo;

        @JacksonXmlProperty(localName = "numOfrows")
        private int numOfrows;

        private Items items;
    }

    @Data
    public static class Items {
        @JacksonXmlElementWrapper(useWrapping = false) // item 리스트를 감싸는 태그(items)가 있지만, 내부 아이템은 리스트임을 알려줌
        @JacksonXmlProperty(localName = "item")
        private List<ExhibitionDTO> itemList;
    }

    public List<ExhibitionDTO> getItems() {
        return (body != null && body.items != null) ? body.items.getItemList() : null;
    }
}
