package com.musai.musai.service.exhibition;

import com.musai.musai.dto.exhibition.ExhibitionDTO;
import com.musai.musai.dto.exhibition.DetailApiResponse;
import com.musai.musai.entity.exhibition.ApiFetchStatus;
import com.musai.musai.entity.exhibition.Exhibition;
import com.musai.musai.repository.exhibition.ApiFetchStatusRepository;
import com.musai.musai.repository.exhibition.ExhibitionRepository;
import com.musai.musai.util.DetailApiParser;
import com.musai.musai.util.DistanceCalculator;
import com.musai.musai.util.ExhibitionApiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionApiParser parser;
    private final RestTemplate restTemplate;  // 생성자 주입된 RestTemplate 필드 추가

    @Value("${openapi.key}")
    private String serviceKey; // 이미 URL 인코딩된 값으로 properties에 저장

    private final ApiFetchStatusRepository statusRepository;

    private final int NUM_OF_ROWS = 500;
    private final int FILTER_END_DATE = Integer.parseInt(
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    );

    String keyword = URLEncoder.encode("전시", StandardCharsets.UTF_8);
    String serviceTp = "A";

    private static final String API_URL_TEMPLATE =
            "https://apis.data.go.kr/B553457/cultureinfo/period2?serviceKey=%s&PageNo=%d&numOfrows=%d&to=%s";

    private static final String DETAIL_API_URL_TEMPLATE =
            "https://apis.data.go.kr/B553457/cultureinfo/detail2?serviceKey=%s&seq=%s";

    public List<Exhibition> getAllExhibitions() {
        return exhibitionRepository.findAll();
    }

    public Exhibition getExhibitionById(Long id) {
        return exhibitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("전시회 ID " + id + "가 존재하지 않습니다."));
    }

    public List<Exhibition> searchExhibition(String keyword) {
        return exhibitionRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Exhibition> searchPlace(String place) {
        return exhibitionRepository.findByPlaceContainingIgnoreCase(place);
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

    private List<ExhibitionDTO> callApi(int pageNo, int numOfRows) {
        try {
            String url = String.format(API_URL_TEMPLATE, serviceKey, pageNo, numOfRows, FILTER_END_DATE);
            log.info("Calling API: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_XML));
            headers.set("User-Agent", "Mozilla/5.0");

            URI uri = new URI(url);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
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

    @Transactional
    private void processAndSave(List<ExhibitionDTO> dataList) {
        log.info("Processing {} items", dataList.size());

        for (ExhibitionDTO dto : dataList) {
            try {
                Integer seqnum = (dto.getExhi_id() != null) ? dto.getExhi_id().intValue() : null;

                if (seqnum != null
                        && "전시".equals(dto.getRealmName())
                        && dto.getStartDate() != null && !dto.getStartDate().isBlank()
                        && dto.getEndDate() != null && !dto.getEndDate().isBlank()
                        && !exhibitionRepository.existsBySeqnum(seqnum)) {

                    Exhibition entity = dto.toEntity();
                    entity.setSeqnum(seqnum);
                    exhibitionRepository.save(entity);
                    log.info("Saved exhibition: seqnum={}, title={}, realmName={}", seqnum, dto.getTitle(), dto.getRealmName());
                } else {
                    log.debug("Filtered out or duplicate seqnum found: seqnum={}, realmName={}, startDate={}, endDate={}",
                            seqnum, dto.getRealmName(), dto.getStartDate(), dto.getEndDate());
                }
            } catch (Exception e) {
                log.error("Error saving exhibition: {}", dto, e);
            }
        }
    }

    @Transactional
    public void deleteEndedExhibitions() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        exhibitionRepository.deleteByEndDateBefore(today);
    }

    public String fetchPlaceUrlFromDetailApi(Integer seqnum) {
        try {
            String url = String.format(DETAIL_API_URL_TEMPLATE, serviceKey, seqnum);
            log.info("Calling Detail API URL: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_XML));
            headers.set("User-Agent", "Mozilla/5.0");

            URI uri = new URI(url);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            String xml = response.getBody();

            if (xml == null || xml.isEmpty()) {
                log.warn("Empty response from detail API for seqnum {}", seqnum);
                return null;
            }

            DetailApiParser detailApiParser = new DetailApiParser();
            DetailApiResponse detailResponse = detailApiParser.parse(xml);
            if (detailResponse != null
                    && detailResponse.getBody() != null
                    && detailResponse.getBody().getItems() != null
                    && detailResponse.getBody().getItems().getItem() != null) {
                return detailResponse.getBody().getItems().getItem().getPlaceUrl();
            }

        } catch (Exception e) {
            log.error("Detail API call failed for seqnum " + seqnum, e);
        }
        return null;
    }

    public List<Exhibition> findNearestExhibitions(Double latitude, Double longitude) {
        try {
            List<Exhibition> exhibitionsWithGPS = exhibitionRepository.findExhibitionsWithGPS();
            
            if (exhibitionsWithGPS.isEmpty()) {
                log.warn("GPS 좌표가 있는 전시회가 없습니다.");
                return List.of();
            }

            return exhibitionsWithGPS.stream()
                    .filter(exhibition -> {
                        try {
                            Double.parseDouble(exhibition.getGpsX());
                            Double.parseDouble(exhibition.getGpsY());
                            return true;
                        } catch (NumberFormatException e) {
                            log.warn("Invalid GPS coordinates for exhibition {}: gpsX={}, gpsY={}", 
                                    exhibition.getExhiId(), exhibition.getGpsX(), exhibition.getGpsY());
                            return false;
                        }
                    })
                    .sorted((a, b) -> {
                        double distanceA = DistanceCalculator.calculateDistance(
                                latitude, longitude,
                                Double.parseDouble(a.getGpsY()),
                                Double.parseDouble(a.getGpsX())
                        );
                        double distanceB = DistanceCalculator.calculateDistance(
                                latitude, longitude,
                                Double.parseDouble(b.getGpsY()),
                                Double.parseDouble(b.getGpsX())
                        );
                        return Double.compare(distanceA, distanceB);
                    })
                    .limit(3)
                    .toList();

        } catch (Exception e) {
            log.error("가장 가까운 전시회 조회 중 오류 발생", e);
            return List.of();
        }
    }
}
