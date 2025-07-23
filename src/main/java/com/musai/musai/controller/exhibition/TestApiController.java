package com.musai.musai.controller.exhibition;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RestController
public class TestApiController {

    @GetMapping("/testApi")
    public String callApi() {
        try {
            String rawUrl = "https://apis.data.go.kr/B553457/cultureinfo/period2"
                    + "?serviceKey=Oe776vFAEoDGf8TNfhyLvhKoQojkURElIrrFrILq0QvZoJZeen%2BHqBrnxC7MgbK%2FVbjeqUj%2F5RNGn3kchsTbGw%3D%3D"
                    + "&PageNo=1"
                    + "&numOfRows=10"
                    + "&keyword=%ED%86%A0%EB%B9%84%EC%95%84%EC%8A%A4"
                    + "&gpsxfrom=128.9427422591895"
                    + "&gpsyfrom=35.10921642590141"
                    + "&gpsxto=128.9427422591895"
                    + "&gpsyto=35.10921642590141"
                    + "&serviceTp=A"
                    + "&from=20190616"
                    + "&to=20191231";

            RestTemplate restTemplate = new RestTemplate();

            // ✅ 이 부분 중요: URI 사용
            URI uri = new URI(rawUrl);
            return restTemplate.getForObject(uri, String.class);

        } catch (Exception e) {
            e.printStackTrace();
            return "API 호출 실패: " + e.getMessage();
        }
    }
}
