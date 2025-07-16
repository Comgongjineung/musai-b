package com.musai.musai.service.exhibition;

import com.musai.musai.dto.exhibition.ExhibitionDTO;
import com.musai.musai.entity.exhibition.Exhibition;
import com.musai.musai.repository.exhibition.ExhibitionRepository;
import com.musai.musai.util.ExhibitionApiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExhibitionService {

    private final ExhibitionRepository repository;
    private final ExhibitionApiParser parser;

    @Transactional
    public void syncExhibitions() {
        // 1. 지난 전시 삭제
        repository.deleteByEndDateBefore(LocalDate.now());

        // 2. OpenAPI에서 전시 가져오기
        List<ExhibitionDTO> dtoList = parser.fetchExhibitions();

        for (ExhibitionDTO dto : dtoList) {
            // 3. 중복 체크
            if (repository.existsByTitleAndPeriod(dto.getTitle(), dto.getPeriod())) continue;

            // 4. 날짜 파싱
            LocalDate startDate = null;
            LocalDate endDate = null;
            try {
                String[] dates = dto.getPeriod().split("~");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                startDate = LocalDate.parse(dates[0].trim(), formatter);
                endDate = LocalDate.parse(dates[1].trim(), formatter);
            } catch (Exception e) {
                // 파싱 실패 시 패스
                continue;
            }

            // 5. 저장
            Exhibition entity = Exhibition.builder()
                    .title(dto.getTitle())
                    .cntcInsttNm(dto.getCntc_instt_nm())
                    .description(dto.getDescription())
                    .imageObject(dto.getImage_object())
                    .genre(dto.getGenre())
                    .url(dto.getUrl())
                    .duration(dto.getDuration())
                    .period(dto.getPeriod())
                    .eventPeriod(dto.getEvent_period())
                    .tableOfContents(dto.getTable_of_contents())
                    .eventSite(dto.getEvent_site())
                    .contributor(dto.getContributor())
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            repository.save(entity);
        }
    }
}
