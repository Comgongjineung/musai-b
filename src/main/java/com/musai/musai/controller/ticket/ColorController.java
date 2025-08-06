package com.musai.musai.controller.ticket;

import com.musai.musai.dto.ticket.ColorDTO;
import com.musai.musai.service.ticket.ColorService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/Ticketcolor")
public class ColorController {

    @Autowired
    private ColorService colorService;

    @Operation(summary = "티켓 색상 추천", description = "티켓 배경 색상을 추천합니다.")
    @PostMapping(value = "/recommend-color", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ColorDTO recommendColor(@RequestPart("image") MultipartFile image) throws Exception {
        return colorService.getColorFromAiServer(image);
    }
}