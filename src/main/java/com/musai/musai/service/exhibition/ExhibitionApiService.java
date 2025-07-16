package com.musai.musai.service.exhibition;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class ExhibitionApiService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.url}")
    private String apiUrl;

    @Value("${api.key}")
    private String apiKey;

    public String getRawApiData(int pageNo, int numOfRows) {
        String url = apiUrl + "?serviceKey=" + apiKey + "&numOfRows=" + numOfRows + "&pageNo=" + pageNo;
        return restTemplate.getForObject(url, String.class);
    }
}
