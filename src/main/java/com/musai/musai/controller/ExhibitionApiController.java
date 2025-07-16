package com.musai.musai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.musai.musai.service.ExhibitionApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExhibitionApiController {
    private final ExhibitionApiService exhibitionApiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/api/raw")
    public String getRawApi(@RequestParam(defaultValue = "1") int pageNo,
                            @RequestParam(defaultValue = "10") int numOfRows) throws Exception {
        String rawJson = exhibitionApiService.getRawApiData(pageNo, numOfRows);
        Object json = objectMapper.readValue(rawJson, Object.class);
        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
        return writer.writeValueAsString(json);
    }
}