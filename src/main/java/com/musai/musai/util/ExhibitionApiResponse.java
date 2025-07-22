package com.musai.musai.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class Body {
        private int totalCount;
        private int pageNo;
        private int numOfRows;

        private Items items;
    }

    @Data
    public static class Items {
        @JsonProperty("item")
        private List<ExhibitionDTO> itemList;
    }

    // 응답에서 item 리스트 반환
    public List<ExhibitionDTO> getItems() {
        return (body != null && body.items != null) ? body.items.itemList : null;
    }
}
