package com.musai.musai.util;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.musai.musai.dto.exhibition.DetailApiResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class DetailApiParser {

    public DetailApiResponse parse(String xmlData) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(xmlData, DetailApiResponse.class);
        } catch (IOException e) {
            log.error("Detail API XML Parsing Error", e);
            return null;
        }
    }
}
