package com.musai.musai.service.arts;

import com.musai.musai.dto.arts.MetDto;
import com.musai.musai.entity.arts.Met;
import com.musai.musai.repository.arts.MetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetService {

    private final Logger logger = LoggerFactory.getLogger(MetService.class);
    private final MetRepository metRepository;
    private final WebClient webClient;

    public MetService(MetRepository metRepository) {
        this.metRepository = metRepository;
        this.webClient = WebClient.builder()
                .baseUrl("https://collectionapi.metmuseum.org/public/collection/v1")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024)) // 16MB 버퍼 크기
                        .build())
                .build();
    }

    public void fetchAndSaveAllObjects() {
        logger.info("Fetching object IDs from Met API...");
        MetDto response = webClient.get()
                .uri("/objects")
                .retrieve()
                .bodyToMono(MetDto.class)
                .block();

        if (response != null && response.getObjectIDs() != null) {
            List<Long> objectIds = response.getObjectIDs();
            logger.info("Fetched {} object IDs", objectIds.size());

            final int batchSize = 1000;
            for (int i = 0; i < objectIds.size(); i += batchSize) {
                int end = Math.min(i + batchSize, objectIds.size());
                List<Long> batchList = objectIds.subList(i, end);

                saveBatch(batchList);

                try {
                    Thread.sleep(1000); // 배치 사이에 1초 쉬기
                } catch (InterruptedException e) {
                    logger.error("Interrupted while sleeping between batches", e);
                    Thread.currentThread().interrupt();
                }
            }
            logger.info("All object IDs saved to DB.");
        } else {
            logger.warn("No object IDs fetched from Met API.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBatch(List<Long> objectIds) {
        List<Met> entities = objectIds.stream()
                .map(Met::new)
                .collect(Collectors.toList());

        metRepository.saveAll(entities);
        metRepository.flush();  // 즉시 DB 반영
        logger.info("Saved batch of size: {}", entities.size());
        logger.info("배치마다 DB 삽입되었습니다.");

        try {
            Thread.sleep(5000); // 5초 쉬기
        } catch (InterruptedException e) {
            logger.error("Interrupted while sleeping after batch save", e);
            Thread.currentThread().interrupt();
        }
    }


    public List<Met> getAllMetEntities() {
        return metRepository.findAll();
    }
}
