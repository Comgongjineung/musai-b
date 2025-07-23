package com.musai.musai.service.difficulty;

import com.musai.musai.entity.user.DefaultDifficulty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DifficultyService {

    // FastAPI 서버 주소
    private static final String FAST_API_URL = "http://musai-ai:8000/web-detection/";
    private static final String LOCAL_API_URL = "http://localhost:8000/web-detection/";

    public String convert(DefaultDifficulty level, String original) {
        return "변환된 텍스트";
    }
}
