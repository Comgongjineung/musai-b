package com.musai.musai.dto;

import lombok.Data;

@Data
public class VuforiaResponseDTO {
    private String targetId;
    private String status;
    private String message;
    private boolean success;
    
    public static VuforiaResponseDTO success(String targetId) {
        VuforiaResponseDTO response = new VuforiaResponseDTO();
        response.setTargetId(targetId);
        response.setStatus("SUCCESS");
        response.setSuccess(true);
        return response;
    }
    
    public static VuforiaResponseDTO error(String message) {
        VuforiaResponseDTO response = new VuforiaResponseDTO();
        response.setMessage(message);
        response.setStatus("ERROR");
        response.setSuccess(false);
        return response;
    }
} 