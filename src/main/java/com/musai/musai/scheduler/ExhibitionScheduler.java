package com.musai.musai.scheduler;

import com.musai.musai.service.exhibition.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExhibitionScheduler {

    private final ExhibitionService service;

    //매주 월요일 12시에 openAPI 연결
    @Scheduled(cron = "0 0 12 ? * MON", zone = "Asia/Seoul")
    public void updateExhibitionData() {
//        service.syncExhibitions();
    }

    //매일 0시에 만료된 전시회 삭제
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void deleteEndedExhibitions() {
        //
    }
}
