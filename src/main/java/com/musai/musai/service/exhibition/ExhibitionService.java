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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionApiParser parser;

    @Value("${openapi.key}")
    private String serviceKey;

    @Autowired
    private ApiFetchStatusRepository statusRepository;

    private final int NUM_OF_ROWS = 500;
    private final int FILTER_END_DATE = 20250721;

    private static final String API_URL_TEMPLATE =
            "https://apis.data.go.kr/B553457/cultureinfo/period2?serviceKey=%s&pageNo=%d&numOfRows=%d&to=%s";

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Exhibition> getAllExhibitions() {
        return exhibitionRepository.findAll();
    }

    public Exhibition getExhibitionById(Long id) {
        return exhibitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("전시회 ID " + id + "가 존재하지 않습니다."));
    }

    // 마지막 저장한 페이지 번호 가져오기
    public int getLastPageNo() {
        return statusRepository.findAll().stream()
                .findFirst()
                .map(ApiFetchStatus::getLastPageNo)
                .orElse(0);
    }

    // 마지막 저장 페이지 번호 업데이트
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

    // API 호출, 필터링, 중복 체크, 저장, 이어받기 포함 전체 작업
    public void fetchAndSaveAllExhibitions() {
        int pageNo = getLastPageNo() + 1;  // 마지막 페이지 다음부터 시작

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

    // API 호출 및 XML 파싱
    private List<ExhibitionDTO> callApi(int pageNo, int numOfRows) {
        try {
            // ✅ properties에서 가져온 원본 키 → 한 번만 인코딩
            String encodedKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);
            String url = String.format(API_URL_TEMPLATE, encodedKey, pageNo, numOfRows, FILTER_END_DATE);

            log.info("Raw Service Key: {}", serviceKey);
            log.info("Encoded Service Key: {}", encodedKey);

            log.info("Calling API: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_XML));
            headers.set("User-Agent", "Mozilla/5.0");

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            String responseXml = response.getBody();

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


    // 필터링 및 저장 처리 (exhi_id 기준 중복 체크)
    @Transactional
    private void processAndSave(List<ExhibitionDTO> dataList) {
        for (ExhibitionDTO dto : dataList) {
            try {
                int endDateInt = Integer.parseInt(dto.getEndDate());
                if (endDateInt >= FILTER_END_DATE) {
                    Long exhiId = dto.getExhi_id();
                    if (exhiId != null && !exhibitionRepository.existsById(exhiId)) {
                        exhibitionRepository.save(dto.toEntity());
                        log.info("Saved exhibition: exhi_id={}, title={}", exhiId, dto.getTitle());
                    } else {
                        log.debug("Exhibition already exists: exhi_id={}", exhiId);
                    }
                } else {
                    log.debug("Filtered out exhibition by endDate: {}", dto.getEndDate());
                }
            } catch (NumberFormatException e) {
                log.error("Invalid endDate format: {}", dto.getEndDate());
            }
        }
    }
}
