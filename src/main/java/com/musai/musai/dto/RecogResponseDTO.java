package com.musai.musai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecogResponseDTO {
    private String vision_result;
    private String gemini_result;
}
