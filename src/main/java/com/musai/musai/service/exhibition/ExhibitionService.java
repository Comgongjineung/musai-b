package com.musai.musai.service.exhibition;

import com.musai.musai.dto.exhibition.ExhibitionDTO;
import com.musai.musai.entity.exhibition.ApiFetchStatus;
import com.musai.musai.entity.exhibition.Exhibition;
import com.musai.musai.repository.exhibition.ApiFetchStatusRepository;
import com.musai.musai.repository.exhibition.ExhibitionRepository;
import com.musai.musai.util.ExhibitionApiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionApiParser parser;

    @Value("${openapi.key}")
    private String serviceKey; // ✅ 이미 URL 인코딩된 값으로 properties에 저장

    @Autowired
    private ApiFetchStatusRepository statusRepository;

    private final int NUM_OF_ROWS = 500;
    private final int FILTER_END_DATE = 20250721;

    private static final String API_URL_TEMPLATE =
            "https://apis.data.go.kr/B553457/cultureinfo/period2?serviceKey=%s&PageNo=%d&numOfrows=%d&keyword=전시&serviceTp=A&to=%s";

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new org.springframework.http.converter.StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    public List<Exhibition> getAllExhibitions() {
        return exhibitionRepository.findAll();
    }

    public Exhibition getExhibitionById(Long id) {
        return exhibitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("전시회 ID " + id + "가 존재하지 않습니다."));
    }

    public int getLastPageNo() {
        return statusRepository.findAll().stream()
                .findFirst()
                .map(ApiFetchStatus::getLastPageNo)
                .orElse(0);
    }

    @Transactional
    public void updateLastPageNo(int pageNo) {
        List<ApiFetchStatus> list = statusRepository.findAll();
        ApiFetchStatus status;
        if (list.isEmpty()) {
            status = new ApiFetchStatus(pageNo);
        } else {
            status = list.get(0);
            status.setLastPageNo(pageNo);
        }
        statusRepository.save(status);
    }

    // ✅ 전체 API 데이터 저장
    public void fetchAndSaveAllExhibitions() {
        int pageNo = getLastPageNo() + 1;

        while (true) {
            List<ExhibitionDTO> dataList = callApi(pageNo, NUM_OF_ROWS);

            if (dataList == null || dataList.isEmpty()) {
                log.info("No more data found or null at page {}", pageNo);
                break;
            }

            processAndSave(dataList);
            updateLastPageNo(pageNo);

            if (dataList.size() < NUM_OF_ROWS) {
                log.info("Last page reached: {}", pageNo);
                break;
            }

            pageNo++;
        }
    }

    // ✅ API 호출
    private List<ExhibitionDTO> callApi(int pageNo, int numOfRows) {
        try {
            String url = String.format(API_URL_TEMPLATE, serviceKey, pageNo, numOfRows, FILTER_END_DATE);

            log.info("Calling API: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_XML));
            headers.set("User-Agent", "Mozilla/5.0");

            URI uri = new URI(url);

            ResponseEntity<String> response = restTemplate().exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            String responseXml = response.getBody();

            log.info("Response XML length: {}", (responseXml != null ? responseXml.length() : 0));


            log.info("Response XML: {}", responseXml);

            if (responseXml == null || responseXml.isEmpty()) {
                log.warn("Empty XML response");
                return List.of();
            }

            List<ExhibitionDTO> list = parser.parse(responseXml);
            log.info("Parsed list size: {}", list.size());
            return list;

        } catch (Exception e) {
            log.error("API call failed", e);
            return List.of();
        }
    }

    // ✅ DB 저장
    @Transactional
    private void processAndSave(List<ExhibitionDTO> dataList) {
        log.info("Processing {} items", dataList.size());

        for (ExhibitionDTO dto : dataList) {
            try {
                Integer seqnum = (dto.getExhi_id() != null) ? dto.getExhi_id().intValue() : null;

                if (seqnum != null && !exhibitionRepository.existsBySeqnum(seqnum)) {
                    Exhibition entity = dto.toEntity();
                    entity.setSeqnum(seqnum);
                    exhibitionRepository.save(entity);
                    log.info("Saved exhibition: seqnum={}, title={}", seqnum, dto.getTitle());
                } else {
                    log.debug("Duplicate or null seqnum found: seqnum={}", seqnum);
                }
            } catch (Exception e) {
                log.error("Error saving exhibition: {}", dto, e);
            }
        }
    }

}
