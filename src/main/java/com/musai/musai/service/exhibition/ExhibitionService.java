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

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionApiParser parser;

    public List<Exhibition> getAllExhibitions() {
        return exhibitionRepository.findAll();
    }

    public Exhibition getExhibitionById(Long id) {
        return exhibitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("전시회 ID " + id + "가 존재하지 않습니다."));
    }

    //새로운 전시회 추가
    public void newExhibitions(List<ExhibitionDTO> exhibitions) {
        for (ExhibitionDTO dto : exhibitions) {
            if (!exhibitionRepository.existsByExhiId(dto.getExhi_id())) {

            }
        }
    }

    // 종료된 전시회 삭제
    public void deleteEndedExhibitions() {
        LocalDate today = LocalDate.now();
        exhibitionRepository.deleteByEndDateBefore(today.toString());
    }
}
