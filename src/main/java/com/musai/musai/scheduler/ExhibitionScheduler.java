package com.musai.musai.scheduler;

import com.musai.musai.service.exhibition.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExhibitionScheduler {

    private final ExhibitionService service;

    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Seoul")
    public void updateExhibitionData() {
//        service.syncExhibitions();
    }
}
