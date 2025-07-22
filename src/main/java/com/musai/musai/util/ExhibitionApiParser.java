package com.musai.musai.util;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.musai.musai.dto.exhibition.ExhibitionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ExhibitionApiParser {

    /**
     * XML 문자열을 ExhibitionDTO 리스트로 파싱
     */
    public List<ExhibitionDTO> parse(String xmlData) {
        List<ExhibitionDTO> result = new ArrayList<>();
        try {
            XmlMapper xmlMapper = new XmlMapper();

            // API 응답 전체를 ExhibitionApiResponse로 매핑
            ExhibitionApiResponse response = xmlMapper.readValue(xmlData, ExhibitionApiResponse.class);

            if (response.getItems() != null) {
                result = response.getItems();
            }
        } catch (IOException e) {
            log.error("XML Parsing Error", e);
        }
        return result;
    }
}
